package ru.hofftech.shared.model.core;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Результат распознавания машин
 *
 * @param machines распознанные мышины
 * @param errors описание ошибок (пустой, если ошибок нет)
 */
@Builder
public record ParserMachineProcessorResult(
        // Посылки, которые удалось распарсить
        @NonNull List<Machine> machines,
        // Описание ошибки при трансформации посылки
        @NonNull List<String> errors) {

    /**
     * Есть ли ошибки при распознавании посылок
     *
     * @return true, если есть ошибки
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @NonNull
    public String getErrorsAsString() {
        return String.join("\n", errors);
    }
}
