package ru.hofftech.unload.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.FileType;

@Getter
@NullMarked
@RequiredArgsConstructor
public enum UnloadOutputType {
    TEXT(1, "Результат выполнения в RESULT для последующего вывода на экран"),
    TEXT_FILE(2, "Результат выполнения в txt файл"),
    JSON_FILE(3, "Результат выполнения в enums файл");

    private final int id;

    private final String description;

    public boolean needSaveFile() {
        return this.loadOutputType2FileType() != null;
    }

    @Nullable
    public FileType loadOutputType2FileType() {
        return switch (this) {
            case TEXT_FILE -> FileType.TXT;
            case JSON_FILE -> FileType.JSON;
            default -> null;
        };
    }

    /**
     * Получает LoadOutputType по строковому представлению (без учёта регистра).
     * Поддерживает как полные названия (TEXT, TEXT_FILE, JSON_FILE),
     *
     * @param text строковое представление типа
     * @return соответствующий LoadOutputType или null, если не найден
     */
    @Nullable
    public static UnloadOutputType fromString(@Nullable String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        String normalized = text.trim().toUpperCase();

        // Поиск по имени enum (TEXT, TEXT_FILE, JSON_FILE)
        for (UnloadOutputType type : values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }
        return null;
    }
}
