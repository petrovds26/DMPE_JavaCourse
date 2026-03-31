package ru.hofftech.load.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

@Getter
@RequiredArgsConstructor
@NullMarked
public enum LoadStrategyParcelInvalidCauseType {
    PARCEL_INVALID("Посылка не прошла валидацию"),
    PARCEL_OVERSIZED("Посылка превышает размеры машины"),
    NO_MACHINE_SPACE("Не хватает места в машинах для доставки");

    private final String description;
}
