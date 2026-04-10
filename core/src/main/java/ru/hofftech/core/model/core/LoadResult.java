package ru.hofftech.core.model.core;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Результат упаковки посылок в машины.
 * <p>
 * Содержит информацию о всех посылках, прошедших обработку,
 * а также о возникших ошибках и успешно упакованных машинах.
 *
 * @param inputParcels                 посылки, поступившие на вход для обработки
 * @param loadStrategyParcelInvalids   посылки, которые не удалось обработать (с указанием причины)
 * @param machines                     машины с успешно упакованными посылками
 * @param errors                       общие ошибки обработки, не привязанные к конкретным посылкам
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
     * Подсчитывает количество машин, в которых есть хотя бы одна посылка.
     *
     * @return количество машин с посылками
     */
    public int getUsedMachinesCount() {
        if (machines == null) {
            return 0;
        }
        return (int) machines.stream()
                .filter(machine -> !machine.parcels().isEmpty())
                .count();
    }

    /**
     * Подсчитывает сумму заполненных клеток всех успешно упакованных посылок.
     *
     * @return общее количество заполненных клеток во всех посылках
     */
    public int getTotalFilledCells() {
        if (machines == null) {
            return 0;
        }
        return machines.stream()
                .flatMap(machine -> machine.parcels().stream())
                .mapToInt(placedParcel -> placedParcel.parcel().getFilledCellsCount())
                .sum();
    }

    /**
     * Подсчитывает количество посылок в конкретной машине.
     *
     * @param machine машина для подсчёта
     * @return количество посылок в машине
     */
    private int countParcelsInMachine(Machine machine) {
        return machine.parcels().size();
    }
}
