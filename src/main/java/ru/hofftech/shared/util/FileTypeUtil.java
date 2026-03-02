package ru.hofftech.shared.util;

import lombok.experimental.UtilityClass;
import ru.hofftech.shared.model.enums.FileType;

import java.util.Arrays;

/**
 * Утилитный класс для работы с типами файлов.
 * Предоставляет методы для определения {@link FileType} по расширению или имени файла.
 */
@UtilityClass
public class FileTypeUtil {
    /**
     * Определяет тип файла по расширению.
     *
     * @param extension расширение файла (без точки, например "json", "txt")
     * @return соответствующий {@link FileType} или null, если расширение не поддерживается
     */
    public static FileType fromExtension(String extension) {
        if (extension == null || extension.isBlank()) {
            return null;
        }

        return Arrays.stream(FileType.values())
                .filter(type -> type.getExtension().equalsIgnoreCase(extension))
                .findFirst()
                .orElse(null);
    }

    /**
     * Определяет тип файла по полному имени файла.
     * Извлекает расширение и делегирует {@link #fromExtension(String)}.
     *
     * @param filename имя файла (например, "data.json", "documents/parcels.txt")
     * @return соответствующий {@link FileType} или null, если расширение не поддерживается
     */
    public static FileType fromFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }

        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return null; // нет расширения
        }

        String extension = filename.substring(lastDot + 1);
        return fromExtension(extension);
    }
}
