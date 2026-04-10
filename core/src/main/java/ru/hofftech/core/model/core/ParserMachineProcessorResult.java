package ru.hofftech.core.model.core;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Результат распознавания машин
 *
 * @param machines распознанные мышины
 * @param errors описание ошибок (пустой, если ошибок нет)
 */
@NullMarked
@Builder
public record ParserMachineProcessorResult(
        // Машины, которые удалось распарсить
        List<Machine> machines,
        // Описание ошибки при трансформации посылки
        List<String> errors) {

    /**
     * Есть ли ошибки при распознавании посылок
     *
     * @return true, если есть ошибки
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public String getErrorsAsString() {
        return String.join("\n", errors);
    }
}
