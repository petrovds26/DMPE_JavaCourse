package ru.hofftech.console.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.FileType;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Типы вывода результата операции загрузки.
 */
@Getter
@RequiredArgsConstructor
@NullMarked
public enum LoadOutputType {
    TEXT(1, "Результат выполнения в RESULT для последующего вывода на экран"),
    TEXT_FILE(2, "Результат выполнения в txt файл"),
    JSON_FILE(3, "Результат выполнения в json файл");

    private final int id;

    private final String description;

    /**
     * Проверяет, требует ли тип вывода сохранения в файл.
     *
     * @return true если требуется сохранение в файл
     */
    public boolean needSaveFile() {
        return this.loadOutputType2FileType() != null;
    }

    /**
     * Преобразует тип вывода в тип файла.
     *
     * @return тип файла или null, если вывод не в файл
     */
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
     *
     * @param text строковое представление типа
     * @return соответствующий тип или null, если не найден
     */
    @Nullable
    public static LoadOutputType fromString(@Nullable String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        String normalized = text.trim().toUpperCase();

        // Поиск по имени enum (TEXT, TEXT_FILE, JSON_FILE)
        for (LoadOutputType type : values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Возвращает строку со списком всех доступных типов вывода.
     *
     * @param delimiter разделитель между типами
     * @return строка со списком типов в формате "NAME - description"
     */
    public static String allLoadOutputType(String delimiter) {
        return Arrays.stream(values())
                .map(loadOutputType -> String.format("%s - %s", loadOutputType.name(), loadOutputType.description))
                .collect(Collectors.joining(delimiter));
    }
}
