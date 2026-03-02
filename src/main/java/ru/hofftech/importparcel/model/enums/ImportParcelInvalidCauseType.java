package ru.hofftech.importparcel.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImportParcelInvalidCauseType {
    PARCEL_INVALID("Посылка не прошла валидацию"),
    PARCEL_OVERSIZED("Посылка превышает размеры машины"),
    NO_MACHINE_SPACE("Не хватает места в машинах для доставки");

    private final String description;
}
