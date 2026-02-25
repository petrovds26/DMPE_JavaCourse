package ru.hofftech.service.loader.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.model.core.Machine;
import ru.hofftech.model.core.Parcel;
import ru.hofftech.model.dto.LoadingResult;
import ru.hofftech.service.loader.strategy.ParcelLoadingStrategy;
import ru.hofftech.service.loader.strategy.ParcelLoadingStrategyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Простейший алгоритм: каждая посылка размещается в отдельной машине
 * Посылка размещается в левом верхнем углу (0,0)
 */
@Slf4j
public class OneParcelPerMachineStrategy implements ParcelLoadingStrategy {

    @Override
    public LoadingResult loadParcels(List<Parcel> parcels) {
        List<Machine> machines = new ArrayList<>();
        List<Parcel> oversizedParcels = new ArrayList<>();

        for (Parcel parcel : parcels) {
            // Проверяем, влезает ли посылка в машину
            if (parcel.getWidth() <= Machine.DEFAULT_WIDTH && parcel.getHeight() <= Machine.DEFAULT_HEIGHT) {

                Machine machine = new Machine();
                machine = machine.placeParcel(parcel, 0, 0);
                machines.add(machine);

                log.debug(
                        "Посылка {}x{} размещена в новой машине в позиции (0,0)",
                        parcel.getWidth(),
                        parcel.getHeight());
            } else {
                oversizedParcels.add(parcel);
                log.warn(
                        "Посылка {}x{} слишком велика для машины {}x{} и будет отложена",
                        parcel.getWidth(),
                        parcel.getHeight(),
                        Machine.DEFAULT_WIDTH,
                        Machine.DEFAULT_HEIGHT);
            }
        }

        LoadingResult result = LoadingResult.builder()
                .machines(machines)
                .oversizedParcels(oversizedParcels)
                .build();

        log.debug(
                "Упаковано {} посылок в {} машин. Отложено {} неподходящих посылок",
                result.getTotalParcelsProcessed(),
                machines.size(),
                oversizedParcels.size());

        return result;
    }

    @Override
    public ParcelLoadingStrategyType getAlgorithmType() {
        return ParcelLoadingStrategyType.ONE_PARCEL_PER_MACHINE;
    }
}
