package ru.hofftech.shared.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * Типы причин, по которым посылка не была обработана при загрузке.
 * <p>
 * Используется для классификации проблемных посылок в отчётах.
 */
@Getter
@RequiredArgsConstructor
@NullMarked
public enum LoadStrategyParcelInvalidCauseType {
    PARCEL_INVALID("Посылка не прошла валидацию"),
    PARCEL_OVERSIZED("Посылка превышает размеры машины"),
    NO_MACHINE_SPACE("Не хватает места в машинах для доставки");

    private final String description;
}
