package ru.hofftech.importparcel.service.loader.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.importparcel.model.core.ImportParcelInvalid;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.model.enums.ImportParcelInvalidCauseType;
import ru.hofftech.importparcel.service.loader.strategy.ParcelLoadingStrategy;
import ru.hofftech.importparcel.service.loader.strategy.ParcelLoadingStrategyType;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Простейший алгоритм: каждая посылка размещается в отдельной машине
 * Посылка размещается в левом верхнем углу (0,0)
 */
@Slf4j
public class OneParcelPerMachineStrategy implements ParcelLoadingStrategy {

    @Override
    public ImportParcelResult loadParcels(List<Parcel> parcels, List<Machine> machines) {
        List<ImportParcelInvalid> importParcelInvalids = new ArrayList<>();
        List<Machine> resultMachines = new ArrayList<>(machines);

        for (Parcel parcel : parcels) {
            // Проверяем, есть ли пустые машины
            boolean emptyMachine =
                    resultMachines.stream().anyMatch(m -> m.parcels().isEmpty());
            if (!emptyMachine) {
                importParcelInvalids.add(invalidParcel(
                        parcel,
                        ImportParcelInvalidCauseType.NO_MACHINE_SPACE,
                        String.format(
                                "Для посылки %dx%d нет пустых машин. Посылка будет отложена",
                                parcel.getWidth(), parcel.getHeight())));
                continue;
            }

            // Ищем подходящую машину
            Machine suitableMachine = findSuitableMachine(resultMachines, parcel);

            if (suitableMachine != null) {
                // Размещаем в существующей машине
                int index = resultMachines.indexOf(suitableMachine);
                Machine updatedMachine = suitableMachine.placeParcel(parcel, 0, 0);
                resultMachines.set(index, updatedMachine);

                log.debug(
                        "Посылка {}x{} размещена в существующей машине в позиции (0,0)",
                        parcel.getWidth(),
                        parcel.getHeight());
            } else {
                importParcelInvalids.add(invalidParcel(
                        parcel,
                        ImportParcelInvalidCauseType.PARCEL_OVERSIZED,
                        String.format(
                                "Посылка %dx%d слишком велика для оставшихся пустых машин и будет отложена",
                                parcel.getWidth(), parcel.getHeight())));
            }
        }

        ImportParcelResult result = ImportParcelResult.builder()
                .machines(resultMachines)
                .importParcelInvalids(importParcelInvalids)
                .build();

        log.debug(
                "Упаковано {} посылок в {} машин. Отложено {} неподходящих посылок",
                result.getTotalParcelsProcessed(),
                resultMachines.size(),
                importParcelInvalids.size());

        return result;
    }

    private Machine findSuitableMachine(List<Machine> machines, Parcel parcel) {
        return machines.stream()
                .filter(m -> m.parcels().isEmpty() && m.fitsInMachine(parcel))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ParcelLoadingStrategyType getAlgorithmType() {
        return ParcelLoadingStrategyType.ONE_PARCEL_PER_MACHINE;
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
}
