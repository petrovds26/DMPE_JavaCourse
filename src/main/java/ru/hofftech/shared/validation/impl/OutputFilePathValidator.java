package ru.hofftech.shared.validation.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.validation.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Валидатор для пути к выходному файлу.
 * Проверяет:
 * <ul>
 *   <li>Путь не пустой</li>
 *   <li>Директория для сохранения существует</li>
 *   <li>Директория доступна для записи</li>
 *   <li>Файл (если существует) доступен для записи</li>
 *   <li>Возможность создания файла</li>
 * </ul>
 */
@Slf4j
public class OutputFilePathValidator implements Validator<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public List<String> validate(@Nullable String filePath) {
        List<String> errors = new ArrayList<>();

        if (filePath == null || filePath.isBlank()) {
            errors.add("Путь к выходному файлу не может быть пустым");
            return errors;
        }

        Path path = Paths.get(filePath);
        Path parentDir = path.getParent();

        validateParentDirectoryExists(parentDir, errors);
        validateParentDirectoryWritable(parentDir, errors);
        validateExistingFileWritable(path, filePath, errors);
        validateCanCreateFile(path, parentDir, errors);

        return errors;
    }

    private void validateParentDirectoryExists(@Nullable Path parentDir, @NonNull List<String> errors) {
        if (parentDir != null && !Files.exists(parentDir)) {
            errors.add("Директория для сохранения не существует: " + parentDir);
        }
    }

    private void validateParentDirectoryWritable(@Nullable Path parentDir, @NonNull List<String> errors) {
        if (parentDir != null && Files.exists(parentDir) && !Files.isWritable(parentDir)) {
            errors.add("Нет прав на запись в директорию: " + parentDir);
        }
    }

    private void validateExistingFileWritable(
            @NonNull Path path, @NonNull String filePath, @NonNull List<String> errors) {
        if (!Files.exists(path)) {
            return;
        }

        if (!Files.isWritable(path)) {
            errors.add("Файл существует и защищён от записи: " + filePath);
        } else {
            log.warn("Файл {} уже существует и будет перезаписан", filePath);
        }
    }

    private void validateCanCreateFile(@NonNull Path path, @Nullable Path parentDir, @NonNull List<String> errors) {
        if (Files.exists(path)) {
            return;
        }

        Path directory = parentDir != null ? parentDir : Paths.get(".");

        try {
            Path testFile = Files.createTempFile(directory, "test_", ".tmp");
            Files.delete(testFile);
        } catch (IOException e) {
            errors.add("Не удаётся создать файл в указанной директории: " + e.getMessage());
        } catch (SecurityException e) {
            errors.add("Недостаточно прав для создания файла в директории: " + directory + ": " + e.getMessage());
        }
    }
}
