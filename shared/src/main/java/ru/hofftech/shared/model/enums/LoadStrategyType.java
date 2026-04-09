package ru.hofftech.shared.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Типы стратегий загрузки посылок в машины.
 * <p>
 * Определяет доступные алгоритмы упаковки посылок.
 */
@Getter
@RequiredArgsConstructor
@NullMarked
public enum LoadStrategyType {
    ONE_PARCEL_PER_MACHINE(1, "Одна посылка на машину"),
    DENSE_PACKING(2, "Плотная укладка"),
    BALANCED_PACKING(3, "Равномерная погрузка");

    private final int id;

    private final String description;

    /**
     * Находит стратегию по имени enum.
     *
     * @param name имя стратегии (ONE_PARCEL_PER_MACHINE, DENSE_PACKING, BALANCED_PACKING)
     * @return найденная стратегия или null, если стратегия не найдена
     */
    @Nullable
    public static LoadStrategyType fromName(String name) {
        if (name.isBlank()) {
            return null;
        }
        try {
            return LoadStrategyType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Возвращает строку со списком всех доступных стратегий.
     *
     * @param delimiter разделитель между стратегиями
     * @return строка со списком стратегий в формате "NAME - description"
     */
    public static String allStrategies(String delimiter) {
        return Arrays.stream(values())
                .map(strategy -> String.format("%s - %s", strategy.name(), strategy.description))
                .collect(Collectors.joining(delimiter));
    }
}
