package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.enums.LoadStrategyParcelInvalidCauseType;

/**
 * DTO проблемной посылки при загрузке.
 * <p>
 * Используется для передачи информации о посылках,
 * которые не удалось загрузить в машины.
 *
 * @param parcel    DTO посылки
 * @param causeType тип причины проблемы
 */
@Builder
@NullMarked
public record LoadParcelInvalidDto(ParcelDto parcel, LoadStrategyParcelInvalidCauseType causeType) {}
