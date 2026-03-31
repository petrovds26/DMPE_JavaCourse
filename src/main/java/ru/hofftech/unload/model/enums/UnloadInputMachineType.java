package ru.hofftech.unload.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.FileType;

@Getter
@NullMarked
@RequiredArgsConstructor
public enum UnloadInputMachineType {
    JSON_FILE(1, "Загрузка посылок из enum файл");

    private final int id;

    private final String description;

    @Nullable
    public static UnloadInputMachineType fileType2LoadInputParcelType(FileType fileType) {
        if (fileType == FileType.JSON) {
            return JSON_FILE;
        }
        return null;
    }
}
