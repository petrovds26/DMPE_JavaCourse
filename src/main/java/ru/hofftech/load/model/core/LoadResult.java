package ru.hofftech.load.model.core;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.List;

/**
 * Результат упаковки посылок в машины.
 * Содержит информацию о всех посылках, прошедших обработку,
 * а также о возникших ошибках и успешно упакованных машинах.
 *
 * @param inputParcels посылки, поступившие на вход для обработки
 * @param loadStrategyParcelInvalids посылки, которые не удалось обработать
 * @param machines машины с успешно упакованными посылками
 * @param errors общие ошибки обработки, не привязанные к конкретным посылкам
 *
 * @author Hofftech
 * @version 1.0
 */
@Builder
@NullMarked
public record LoadResult(
        @Nullable List<Parcel> inputParcels, // Посылки, поступившие на вход для обработки
        @Nullable
                List<LoadStrategyParcelInvalid>
                        loadStrategyParcelInvalids, // Посылки, которые не удалось обработать (с указанием причины)
        @Nullable List<Machine> machines, // Машины с успешно упакованными посылками
        @Nullable List<String> errors // Общие ошибки обработки, не привязанные к конкретным посылкам
        ) {
    /**
     * Подсчитывает общее количество успешно упакованных посылок.
     *
     * @return количество посылок во всех машинах
     */
    public int getTotalParcelsProcessed() {
        if (machines == null) {
            return 0;
        }
        return machines.stream().mapToInt(this::countParcelsInMachine).sum();
    }

    /**
     * Подсчитывает количество посылок в конкретной машине.
     *
     * @param machine машина для подсчёта (не может быть null)
     * @return количество посылок в машине
     */
    private int countParcelsInMachine(Machine machine) {
        return machine.parcels().size();
    }
}
