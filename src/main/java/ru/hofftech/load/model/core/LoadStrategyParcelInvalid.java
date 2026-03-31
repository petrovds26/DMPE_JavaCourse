package ru.hofftech.load.model.core;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.load.model.enums.LoadStrategyParcelInvalidCauseType;
import ru.hofftech.shared.model.core.Parcel;

/**
 * Информация о посылке, которую не удалось обработать.
 *
 * @param parcel посылка, которую не удалось обработать (не может быть null)
 * @param causeType тип ошибки (не может быть null)
 * @param cause текстовое описание причины (не может быть null)
 *
 */
@Builder
@NullMarked
public record LoadStrategyParcelInvalid(Parcel parcel, LoadStrategyParcelInvalidCauseType causeType, String cause) {}
