package ru.hofftech.core.model.core;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.enums.LoadStrategyParcelInvalidCauseType;

/**
 * Информация о посылке, которую не удалось обработать при загрузке.
 *
 * @param parcel    посылка, которую не удалось обработать
 * @param causeType тип ошибки
 * @param cause     текстовое описание причины
 */
@Builder
@NullMarked
public record LoadStrategyParcelInvalid(Parcel parcel, LoadStrategyParcelInvalidCauseType causeType, String cause) {}
