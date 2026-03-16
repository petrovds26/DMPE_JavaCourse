package ru.hofftech.shared.model.core;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Результат распознавания посылок.
 *
 * @param parcels распознанные посылки (не может быть null)
 * @param errors описание ошибок (не может быть null, пустой список если ошибок нет)
 *
 */
@Builder
public record ParserParcelProcessorResult(
        // Посылки, которые удалось распарсить
        @NonNull List<Parcel> parcels,
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

    /**
     * Возвращает все ошибки в виде одной строки.
     *
     * @return строка с ошибками, разделёнными переносом строки (не может быть null)
     */
    @NonNull
    public String getErrorsAsString() {
        return String.join("\n", errors);
    }
}
