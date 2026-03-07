package ru.hofftech.shared.validation.impl;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.validation.Validator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Валидатор для пути к входному файлу.
 * Проверяет:
 * <ul>
 *   <li>Путь не пустой</li>
 *   <li>Файл существует</li>
 *   <li>Файл доступен для чтения</li>
 *   <li>Путь не указывает на директорию</li>
 * </ul>
 */
public class InputFilePathValidator implements Validator<String> {

    /**
     * Проверяет корректность пути к входному файлу.
     *
     * @param filePath путь к файлу для проверки
     * @return список ошибок (пустой список, если ошибок нет)
     */
    @Override
    public @NonNull List<String> validate(@Nullable String filePath) {
        List<String> errors = new ArrayList<>();

        if (filePath == null || filePath.isBlank()) {
            errors.add("Путь к входному файлу не может быть пустым");
            return errors;
        }

        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            errors.add("Входной файл не существует: " + filePath);
            return errors;
        }

        if (!Files.isReadable(path)) {
            errors.add("Нет прав на чтение входного файла: " + filePath);
        }

        if (Files.isDirectory(path)) {
            errors.add("Указанный путь является директорией, а не файлом: " + filePath);
        }

        return errors;
    }
}
