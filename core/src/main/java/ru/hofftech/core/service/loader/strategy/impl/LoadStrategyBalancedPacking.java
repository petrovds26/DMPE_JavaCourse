package ru.hofftech.core.service.loader.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import ru.hofftech.core.model.core.LoadResult;
import ru.hofftech.core.model.core.LoadStrategyParcelInvalid;
import ru.hofftech.core.model.core.Machine;
import ru.hofftech.core.model.core.Parcel;
import ru.hofftech.core.service.loader.LoadStrategyPositionFinder;
import ru.hofftech.core.service.loader.strategy.LoadStrategy;
import ru.hofftech.shared.model.enums.LoadStrategyParcelInvalidCauseType;
import ru.hofftech.shared.model.enums.LoadStrategyType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Стратегия равномерной погрузки по машинам.
 * Посылки распределяются по машинам по очереди (round-robin).
 * Каждая следующая посылка пытается встать в следующую машину.
 * Если в текущей машине нет места, ищем в следующих.
 * Сортирует посылки по размеру (широкие сначала) для лучшей упаковки.
 */
@Slf4j
@NullMarked
@Component
@RequiredArgsConstructor
public class LoadStrategyBalancedPacking implements LoadStrategy {

    private final LoadStrategyPositionFinder loadStrategyPositionFinder;

    /**
     * {@inheritDoc}
     */
    @Override
    public LoadStrategyType getAlgorithmType() {
        return LoadStrategyType.BALANCED_PACKING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoadResult loadParcels(List<Parcel> parcels, List<Machine> machines) {
        List<LoadStrategyParcelInvalid> loadStrategyParcelInvalids = new ArrayList<>();
        List<Machine> resultMachines = new ArrayList<>(machines);

        // Шаг 1: Сортируем посылки по убыванию ширины (самые широкие сначала)
        List<Parcel> sortedParcels = sortParcelsByWidth(parcels);

        // Шаг 2: Индекс машины для round-robin
        int machineIndex = 0;

        // Шаг 3: Обрабатываем каждую посылку
        for (Parcel parcel : sortedParcels) {
            log.debug("Упаковка посылки {}x{} с символом '{}'", parcel.getWidth(), parcel.getHeight(), parcel.symbol());

            // Пытаемся разместить, начиная с текущей машины
            boolean placed = tryPlaceInMachinesRoundRobin(resultMachines, parcel, machineIndex);

            if (placed) {
                // Если разместили, переходим к следующей машине
                machineIndex = (machineIndex + 1) % resultMachines.size();
            } else {
                // Проверяем, влезает ли посылка хотя бы в одну машину по габаритам
                boolean fitsInAnyMachine = resultMachines.stream().anyMatch(m -> m.fitsInMachine(parcel));

                if (fitsInAnyMachine) {
                    // Посылка влезает, но не нашлось места ни в одной машине
                    loadStrategyParcelInvalids.add(invalidParcel(
                            parcel,
                            LoadStrategyParcelInvalidCauseType.NO_MACHINE_SPACE,
                            "Посылка не влезла в существующие машины, так как не нашлось места"));
                } else {
                    // Посылка не влезает ни в одну машину по габаритам
                    loadStrategyParcelInvalids.add(invalidParcel(
                            parcel,
                            LoadStrategyParcelInvalidCauseType.PARCEL_OVERSIZED,
                            String.format(
                                    "Посылка %dx%d слишком велика для всех доступных машин и будет отложена",
                                    parcel.getWidth(), parcel.getHeight())));
                }
            }
        }

        LoadResult result = LoadResult.builder()
                .machines(resultMachines)
                .loadStrategyParcelInvalids(loadStrategyParcelInvalids)
                .build();

        log.debug(
                "Упаковано {} посылок в {} машин. Отложено {} посылок",
                result.getTotalParcelsProcessed(),
                resultMachines.size(),
                loadStrategyParcelInvalids.size());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAlgorithmName() {
        return "Равномерная погрузка";
    }

    /**
     * Создаёт объект ошибочной посылки с указанной причиной.
     * Также автоматически логирует предупреждение.
     *
     * @param parcel    посылка, которую не удалось обработать (не может быть null)
     * @param causeType тип ошибки (не может быть null)
     * @param cause     текстовое описание причины (не может быть null)
     * @return объект с информацией об ошибке (не может быть null)
     */
    private LoadStrategyParcelInvalid invalidParcel(
            Parcel parcel, LoadStrategyParcelInvalidCauseType causeType, String cause) {
        LoadStrategyParcelInvalid loadStrategyParcelInvalid = LoadStrategyParcelInvalid.builder()
                .parcel(parcel)
                .causeType(causeType)
                .cause(cause)
                .build();

        log.warn(cause);

        return loadStrategyParcelInvalid;
    }

    /**
     * Пытается разместить посылку в машинах по кругу, начиная с указанного индекса.
     *
     * @param machines   список машин (не может быть null)
     * @param parcel     посылка для размещения (не может быть null)
     * @param startIndex индекс машины, с которой начинать поиск
     * @return true если посылка размещена
     */
    private boolean tryPlaceInMachinesRoundRobin(List<Machine> machines, Parcel parcel, int startIndex) {
        if (machines.isEmpty()) {
            return false;
        }

        int size = machines.size();

        // Проходим по всем машинам, начиная с startIndex
        for (int offset = 0; offset < size; offset++) {
            int index = (startIndex + offset) % size;
            Machine machine = machines.get(index);

            // Проверяем, влезает ли посылка в эту машину по габаритам
            if (!machine.fitsInMachine(parcel)) {
                continue;
            }

            int[] position = loadStrategyPositionFinder.findBestPosition(machine, parcel);

            if (position != null) {
                // Размещаем в этой машине
                Machine updatedMachine = machine.placeParcel(parcel, position[0], position[1]);
                machines.set(machines.indexOf(machine), updatedMachine);

                log.debug("Посылка размещена в машине #{} в позиции ({},{})", index + 1, position[0], position[1]);
                return true;
            }
        }

        return false;
    }

    /**
     * Сортирует посылки по убыванию ширины (самые широкие сначала).
     *
     * @param parcels список посылок (не может быть null)
     * @return отсортированный список (не может быть null)
     */
    private List<Parcel> sortParcelsByWidth(List<Parcel> parcels) {
        return parcels.stream()
                .sorted(Comparator.comparingInt(Parcel::getWidth).reversed())
                .toList();
    }
}
