package ru.hofftech.console.service;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.hofftech.console.exception.ConsoleException;
import ru.hofftech.console.exception.ValidateException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Сервис для сохранения строк в файлы.
 * <p>
 * Предоставляет методы для сохранения содержимого в файл
 * с автоматическим созданием родительских директорий.
 */
@Slf4j
@NullMarked
@Service
public class FileSaveService {

    /**
     * Сохраняет строку в файл.
     * <p>
     * При необходимости создаёт родительские директории.
     *
     * @param content  содержимое для сохранения
     * @param filePath путь к файлу
     * @return сообщение об успешном сохранении
     * @throws ValidateException если путь к файлу не указан
     * @throws ConsoleException  если произошла ошибка ввода/вывода или недостаточно прав
     */
    public String saveFile(String content, String filePath) {
        // Проверка входных параметров
        if (filePath.isBlank()) {
            throw new ValidateException("Не указан путь для сохранения файла");
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

            return successMessage;
        } catch (IOException e) {
            throw new ConsoleException(String.format("Ошибка при сохранении файла %s: %s", filePath, e.getMessage()));
        } catch (SecurityException e) {
            throw new ConsoleException(
                    String.format("Недостаточно прав для записи файла %s: %s", filePath, e.getMessage()));
        }
    }
}
