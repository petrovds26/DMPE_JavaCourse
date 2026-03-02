package ru.hofftech.importparcel.model.core;

import lombok.Builder;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.List;

/**
 * Результат упаковки посылок в машины.
 * Содержит информацию о всех посылках, прошедших обработку,
 * а также о возникших ошибках и успешно упакованных машинах.
 */
@Builder
public record ImportParcelResult(
        List<Parcel> inputParcels, // Посылки, поступившие на вход для обработки
        List<ImportParcelInvalid> importParcelInvalids, // Посылки, которые не удалось обработать (с указанием причины)
        List<Machine> machines, // Машины с успешно упакованными посылками
        List<String> errors // Общие ошибки обработки, не привязанные к конкретным посылкам
        ) {
    /**
     * Подсчитывает общее количество успешно упакованных посылок.
     *
     * @return количество посылок во всех машинах
     */
    public int getTotalParcelsProcessed() {
        return machines.stream().mapToInt(this::countParcelsInMachine).sum();
    }

    private int countParcelsInMachine(Machine machine) {
        return machine.parcels().size();
    }
}
