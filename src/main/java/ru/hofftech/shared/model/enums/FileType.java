package ru.hofftech.shared.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Типы входных файлов, поддерживаемые системой
 */
@Getter
@RequiredArgsConstructor
public enum FileType {
    TXT("txt", "Текстовый файл"),
    JSON("json", "JSON файл");

    private final String extension;
    private final String description;
}
