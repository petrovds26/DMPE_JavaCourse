package ru.hofftech.shared.model.core;

import lombok.Builder;
import org.jspecify.annotations.Nullable;

/**
 * Результат трансформации посылки из DTO в сущность.
 *
 * @param parcel созданная посылка или null при ошибке
 * @param error описание ошибки или null при успехе
 */
@Builder
public record TransformParcelResult(
        // Посылки, которые удалось распарсить
        @Nullable Parcel parcel,
        // Описание ошибки при трансформации посылки
        @Nullable String error) {}
