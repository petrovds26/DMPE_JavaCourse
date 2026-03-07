package ru.hofftech.importparcel.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor
public enum ImportParcelInvalidCauseType {
    PARCEL_INVALID("Посылка не прошла валидацию"),
    PARCEL_OVERSIZED("Посылка превышает размеры машины"),
    NO_MACHINE_SPACE("Не хватает места в машинах для доставки");

    @NonNull
    private final String description;
}
