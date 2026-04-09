package ru.hofftech.console.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.FileType;

/**
 * Типы источников данных для загрузки списка посылок.
 */
@Getter
@RequiredArgsConstructor
@NullMarked
public enum LoadInputParcelType {
    TEXT(1, "Загрузка посылок из строки"),
    TEXT_FILE(2, "Загрузка посылок из txt файла"),
    JSON_FILE(3, "Загрузка посылок из enum файл");

    private final int id;

    private final String description;

    /**
     * Преобразует тип файла в тип источника данных для загрузки посылок.
     *
     * @param fileType тип файла
     * @return соответствующий тип источника или null, если преобразование невозможно
     */
    @Nullable
    public static LoadInputParcelType fileType2LoadInputParcelType(FileType fileType) {
        return switch (fileType) {
            case TXT -> LoadInputParcelType.TEXT_FILE;
            case JSON -> LoadInputParcelType.JSON_FILE;
            default -> null;
        };
    }
}
