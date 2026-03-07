package ru.hofftech.shared.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

/**
 * Типы входных файлов, поддерживаемые системой
 */
@Getter
@RequiredArgsConstructor
public enum FileType {
    TXT("txt", "Текстовый файл"),
    JSON("json", "JSON файл");

    @NonNull
    private final String extension;

    @NonNull
    private final String description;
}
