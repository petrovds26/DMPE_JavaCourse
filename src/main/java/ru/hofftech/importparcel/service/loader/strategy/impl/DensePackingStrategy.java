package ru.hofftech.importparcel.service.loader.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
 * Стратегия плотной упаковки посылок
 * - Ищет самое низкое и левое место для каждой посылки
 * - Проверяет опору >50%
 * - Сортирует посылки по размеру (широкие сначала)
 */
@Slf4j
public class DensePackingStrategy implements ParcelLoadingStrategy {

    @NonNull
    private final PositionFinder positionFinder;

    public DensePackingStrategy() {
        this.positionFinder = new PositionFinder();
    }

    @Override
    @NonNull
    public ParcelLoadingStrategyType getAlgorithmType() {
        return ParcelLoadingStrategyType.DENSE_PACKING;
    }

    @Override
    public @NonNull ImportParcelResult loadParcels(@NonNull List<Parcel> parcels, @NonNull List<Machine> machines) {
        List<ImportParcelInvalid> importParcelInvalids = new ArrayList<>();
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
    @NonNull
    public String getAlgorithmName() {
        return "Плотная упаковка";
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
    @NonNull
    private ImportParcelInvalid invalidParcel(
            @NonNull Parcel parcel, @NonNull ImportParcelInvalidCauseType causeType, @NonNull String cause) {
        ImportParcelInvalid importParcelInvalid = ImportParcelInvalid.builder()
                .parcel(parcel)
                .causeType(causeType)
                .cause(cause)
                .build();

        log.warn(cause);

        return importParcelInvalid;
    }

    /**
     * Пытается разместить посылку в одной из существующих машин
     * @return true если посылка размещена
     */
    private boolean tryPlaceInExistingMachines(@NonNull List<Machine> machines, @NonNull Parcel parcel) {
        for (int i = 0; i < machines.size(); i++) {
            Machine machine = machines.get(i);

            // Проверяем, влезает ли посылка в эту машину по габаритам
            if (!machine.fitsInMachine(parcel)) {
                continue;
            }

            int[] position = positionFinder.findBestPosition(machine, parcel);

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
     * Сортирует посылки по убыванию ширины (самые широкие сначала)
     */
    @NonNull
    private List<Parcel> sortParcelsByWidth(@NonNull List<Parcel> parcels) {
        return parcels.stream()
                .sorted(Comparator.comparingInt(Parcel::getWidth).reversed())
                .toList();
    }
}
