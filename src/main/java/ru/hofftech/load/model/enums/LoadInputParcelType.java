package ru.hofftech.load.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.FileType;

@Getter
@RequiredArgsConstructor
public enum LoadInputParcelType {
    TEXT(1, "Загрузка посылок из строки"),
    TEXT_FILE(2, "Загрузка посылок из txt файла"),
    JSON_FILE(3, "Загрузка посылок из enum файл");

    private final int id;

    @NonNull
    private final String description;

    @Nullable
    public static LoadInputParcelType fileType2LoadInputParcelType(@NonNull FileType fileType) {
        return switch (fileType) {
            case TXT -> LoadInputParcelType.TEXT_FILE;
            case JSON -> LoadInputParcelType.JSON_FILE;
            default -> null;
        };
    }
}
