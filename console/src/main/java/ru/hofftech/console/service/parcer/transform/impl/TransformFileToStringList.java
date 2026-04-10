package ru.hofftech.console.service.parcer.transform.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import ru.hofftech.console.exception.ConsoleException;
import ru.hofftech.console.exception.ValidateException;
import ru.hofftech.console.service.parcer.transform.TransformToStringListStrategy;
import ru.hofftech.console.validation.impl.InputFilePathValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Стратегия преобразования файла в список строк.
 * <p>
 * Читает файл построчно и возвращает список строк.
 * Поддерживает как TXT, так и JSON файлы (читает их как текстовые).
 */
@NullMarked
@RequiredArgsConstructor
@Component
@Slf4j
public class TransformFileToStringList implements TransformToStringListStrategy {
    private final InputFilePathValidator inputFilePathValidator;

    /**
     * {@inheritDoc}
     * <p>
     * Валидирует путь к файлу и читает его содержимое построчно.
     *
     * @param inputTxtFileName путь к файлу
     * @return список строк файла
     * @throws ValidateException если файл не проходит валидацию
     * @throws ConsoleException  если произошла ошибка ввода/вывода
     */
    @Override
    public List<String> transform(String inputTxtFileName) {

        List<String> errors = inputFilePathValidator.validate(inputTxtFileName);
        if (!errors.isEmpty()) {
            throw new ValidateException("Ошибки при открытии файла %s".formatted(String.join("; ", errors)));
        }

        log.debug("Чтение Txt файла: {}", inputTxtFileName);

        try {
            return Files.readAllLines(Path.of(inputTxtFileName));
        } catch (IOException e) {
            throw new ConsoleException(
                    String.format("Ошибка при открытии и чтении файла: %s, %s", inputTxtFileName, e.getMessage()));
        }
    }
}
