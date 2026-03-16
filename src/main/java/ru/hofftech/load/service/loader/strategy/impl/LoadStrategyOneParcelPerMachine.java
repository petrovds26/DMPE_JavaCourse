package ru.hofftech.load.service.loader.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.load.model.core.LoadStrategyParcelInvalid;
import ru.hofftech.load.model.enums.LoadStrategyParcelInvalidCauseType;
import ru.hofftech.load.model.enums.LoadStrategyType;
import ru.hofftech.load.service.loader.strategy.LoadStrategy;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Простейший алгоритм: каждая посылка размещается в отдельной машине.
 * Посылка размещается в левом нижнем углу (0,0).
 */
@Slf4j
public class LoadStrategyOneParcelPerMachine implements LoadStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull LoadResult loadParcels(@NonNull List<Parcel> parcels, @NonNull List<Machine> machines) {
        List<LoadStrategyParcelInvalid> loadStrategyParcelInvalids = new ArrayList<>();
        List<Machine> resultMachines = new ArrayList<>(machines);

        for (Parcel parcel : parcels) {
            // Проверяем, есть ли пустые машины
            boolean emptyMachine =
                    resultMachines.stream().anyMatch(m -> m.parcels().isEmpty());
            if (!emptyMachine) {
                loadStrategyParcelInvalids.add(invalidParcel(
                        parcel,
                        LoadStrategyParcelInvalidCauseType.NO_MACHINE_SPACE,
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
                loadStrategyParcelInvalids.add(invalidParcel(
                        parcel,
                        LoadStrategyParcelInvalidCauseType.PARCEL_OVERSIZED,
                        String.format(
                                "Посылка %dx%d слишком велика для оставшихся пустых машин и будет отложена",
                                parcel.getWidth(), parcel.getHeight())));
            }
        }

        LoadResult result = LoadResult.builder()
                .machines(resultMachines)
                .loadStrategyParcelInvalids(loadStrategyParcelInvalids)
                .build();

        log.debug(
                "Упаковано {} посылок в {} машин. Отложено {} неподходящих посылок",
                result.getTotalParcelsProcessed(),
                resultMachines.size(),
                loadStrategyParcelInvalids.size());

        return result;
    }

    /**
     * Находит подходящую пустую машину для посылки.
     *
     * @param machines список машин (не может быть null)
     * @param parcel   посылка для размещения (не может быть null)
     * @return подходящая машина или null, если не найдена
     */
    @Nullable
    private Machine findSuitableMachine(@NonNull List<Machine> machines, @NonNull Parcel parcel) {
        return machines.stream()
                .filter(m -> m.parcels().isEmpty() && m.fitsInMachine(parcel))
                .findFirst()
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public LoadStrategyType getAlgorithmType() {
        return LoadStrategyType.ONE_PARCEL_PER_MACHINE;
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
    @NonNull
    private LoadStrategyParcelInvalid invalidParcel(
            @NonNull Parcel parcel, @NonNull LoadStrategyParcelInvalidCauseType causeType, @NonNull String cause) {
        LoadStrategyParcelInvalid loadStrategyParcelInvalid = LoadStrategyParcelInvalid.builder()
                .parcel(parcel)
                .causeType(causeType)
                .cause(cause)
                .build();

        log.warn(cause);

        return loadStrategyParcelInvalid;
    }
}
