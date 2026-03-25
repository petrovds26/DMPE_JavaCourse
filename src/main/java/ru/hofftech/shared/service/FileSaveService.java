package ru.hofftech.shared.service;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.ProcessorCommandResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Сервис для сохранения строк в файлы.
 *
 */
@Slf4j
@NullMarked
public class FileSaveService {

    /**
     * Сохраняет строку в файл.
     *
     * @param content содержимое для сохранения (не может быть null)
     * @param filePath путь к файлу (не может быть null)
     * @return результат операции с сообщением (не может быть null)
     */
    public ProcessorCommandResult saveFile(String content, String filePath) {
        // Проверка входных параметров
        if (filePath.isBlank()) {
            String errorMessage = "Не указан путь для сохранения файла";
            log.error(errorMessage);
            return ProcessorCommandResult.createFailure(errorMessage);
        }

        try {
            // Создаём родительские директории, если их нет
            Path path = Paths.get(filePath);
            Path parentDir = path.getParent();

            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                log.debug("Созданы директории: {}", parentDir);
            }

            // Сохраняем файл
            Files.writeString(path, content);

            String successMessage = String.format("Файл успешно сохранён: %s", filePath);
            log.info(successMessage);

            return ProcessorCommandResult.createSuccess(successMessage);

        } catch (IOException e) {
            String errorMessage = String.format("Ошибка при сохранении файла %s: %s", filePath, e.getMessage());
            log.error(errorMessage, e);

            return ProcessorCommandResult.createFailure(errorMessage);

        } catch (SecurityException e) {
            String errorMessage = String.format("Недостаточно прав для записи файла %s: %s", filePath, e.getMessage());
            log.error(errorMessage, e);

            return ProcessorCommandResult.createFailure(errorMessage);
        }
    }
}
