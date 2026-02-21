package ru.hofftech.model.dto;

import lombok.Builder;
import ru.hofftech.model.entity.Machine;
import ru.hofftech.model.entity.Parcel;

import java.util.List;

/**
 * Результат упаковки посылок
 */
@Builder
public record LoadingResult(
        List<Parcel> inputParcels, // Посылки, поступившие на вход
        List<Parcel> invalidParcels, // Посылки, которые содержат ошибки на этапе валидации
        List<Machine> machines, // Успешно упакованные машины
        List<Parcel> oversizedParcels // Посылки, которые не влезли в машину
        ) {
    public int getTotalParcelsProcessed() {
        return machines.stream().mapToInt(this::countParcelsInMachine).sum();
    }

    private int countParcelsInMachine(Machine machine) {
        return machine.parcels().size();
    }
}
