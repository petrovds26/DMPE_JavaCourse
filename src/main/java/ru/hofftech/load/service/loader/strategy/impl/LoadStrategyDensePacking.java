package ru.hofftech.load.service.loader.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.load.model.core.LoadStrategyParcelInvalid;
import ru.hofftech.load.model.enums.LoadStrategyParcelInvalidCauseType;
import ru.hofftech.load.model.enums.LoadStrategyType;
import ru.hofftech.load.service.loader.LoadStrategyPositionFinder;
import ru.hofftech.load.service.loader.strategy.LoadStrategy;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Стратегия плотной упаковки посылок.
 * Ищет самое низкое и левое место для каждой посылки.
 * Проверяет опору >50%.
 * Сортирует посылки по размеру (широкие сначала).
 */
@Slf4j
@NullMarked
public class LoadStrategyDensePacking implements LoadStrategy {

    private final LoadStrategyPositionFinder loadStrategyPositionFinder;

    /**
     * Конструктор стратегии плотной упаковки.
     */
    public LoadStrategyDensePacking() {
        this.loadStrategyPositionFinder = new LoadStrategyPositionFinder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoadStrategyType getAlgorithmType() {
        return LoadStrategyType.DENSE_PACKING;
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

        // Шаг 2: Обрабатываем каждую посылку
        for (Parcel parcel : sortedParcels) {
            log.debug("Упаковка посылки {}x{} с символом '{}'", parcel.getWidth(), parcel.getHeight(), parcel.symbol());

            // Пытаемся разместить в существующих машинах
            boolean placed = tryPlaceInExistingMachines(resultMachines, parcel);

            if (!placed) {
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
        return "Плотная упаковка";
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
     * Пытается разместить посылку в одной из существующих машин.
     *
     * @param machines список машин (не может быть null)
     * @param parcel   посылка для размещения (не может быть null)
     * @return true если посылка размещена
     */
    private boolean tryPlaceInExistingMachines(List<Machine> machines, Parcel parcel) {
        for (int i = 0; i < machines.size(); i++) {
            Machine machine = machines.get(i);

            // Проверяем, влезает ли посылка в эту машину по габаритам
            if (!machine.fitsInMachine(parcel)) {
                continue;
            }

            int[] position = loadStrategyPositionFinder.findBestPosition(machine, parcel);

            if (position != null) {
                // Размещаем в этой машине
                Machine updatedMachine = machine.placeParcel(parcel, position[0], position[1]);
                machines.set(machines.indexOf(machine), updatedMachine);

                log.debug(
                        "Посылка размещена в существующей машине #{} в позиции ({},{})",
                        i + 1,
                        position[0],
                        position[1]);
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
