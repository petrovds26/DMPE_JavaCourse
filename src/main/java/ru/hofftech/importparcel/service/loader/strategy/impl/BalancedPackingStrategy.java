package ru.hofftech.importparcel.service.loader.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.importparcel.model.core.ImportParcelInvalid;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.model.enums.ImportParcelInvalidCauseType;
import ru.hofftech.importparcel.service.loader.PositionFinder;
import ru.hofftech.importparcel.service.loader.strategy.ParcelLoadingStrategy;
import ru.hofftech.importparcel.service.loader.strategy.ParcelLoadingStrategyType;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Стратегия равномерной погрузки по машинам
 * - Посылки распределяются по машинам по очереди (round-robin)
 * - Каждая следующая посылка пытается встать в следующую машину
 * - Если в текущей машине нет места, ищем в следующих
 * - Сортирует посылки по размеру (широкие сначала) для лучшей упаковки
 */
@Slf4j
public class BalancedPackingStrategy implements ParcelLoadingStrategy {

    private final PositionFinder positionFinder;

    public BalancedPackingStrategy() {
        this.positionFinder = new PositionFinder();
    }

    @Override
    public ParcelLoadingStrategyType getAlgorithmType() {
        return ParcelLoadingStrategyType.BALANCED_PACKING;
    }

    @Override
    public ImportParcelResult loadParcels(List<Parcel> parcels, List<Machine> machines) {
        List<ImportParcelInvalid> importParcelInvalids = new ArrayList<>();
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
                    importParcelInvalids.add(invalidParcel(
                            parcel,
                            ImportParcelInvalidCauseType.NO_MACHINE_SPACE,
                            "Посылка не влезла в существующие машины, так как не нашлось места"));
                } else {
                    // Посылка не влезает ни в одну машину по габаритам
                    importParcelInvalids.add(invalidParcel(
                            parcel,
                            ImportParcelInvalidCauseType.PARCEL_OVERSIZED,
                            String.format(
                                    "Посылка %dx%d слишком велика для всех доступных машин и будет отложена",
                                    parcel.getWidth(), parcel.getHeight())));
                }
            }
        }

        ImportParcelResult result = ImportParcelResult.builder()
                .machines(resultMachines)
                .importParcelInvalids(importParcelInvalids)
                .build();

        log.debug(
                "Упаковано {} посылок в {} машин. Отложено {} посылок",
                result.getTotalParcelsProcessed(),
                resultMachines.size(),
                importParcelInvalids.size());

        return result;
    }

    @Override
    public String getAlgorithmName() {
        return "Равномерная погрузка";
    }

    /**
     * Создаёт объект ошибочной посылки с указанной причиной.
     * Также автоматически логирует предупреждение.
     *
     * @param parcel    посылка, которую не удалось обработать
     * @param causeType тип ошибки
     * @param cause     текстовое описание причины
     * @return объект {@link ImportParcelInvalid} с информацией об ошибке
     */
    private ImportParcelInvalid invalidParcel(Parcel parcel, ImportParcelInvalidCauseType causeType, String cause) {
        ImportParcelInvalid importParcelInvalid = ImportParcelInvalid.builder()
                .parcel(parcel)
                .causeType(causeType)
                .cause(cause)
                .build();

        log.warn(cause);

        return importParcelInvalid;
    }

    /**
     * Пытается разместить посылку в машинах по кругу, начиная с указанного индекса
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

            int[] position = positionFinder.findBestPosition(machine, parcel);

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
     * Сортирует посылки по убыванию ширины (самые широкие сначала)
     */
    private List<Parcel> sortParcelsByWidth(List<Parcel> parcels) {
        return parcels.stream()
                .sorted(Comparator.comparingInt(Parcel::getWidth).reversed())
                .toList();
    }
}
