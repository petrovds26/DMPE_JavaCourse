package ru.hofftech.shared.service.parser;

import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.ParserParcelProcessorResult;

/**
 * Интерфейс для получения списка посылок.
 *
 * @param <T> тип источника данных (String для строки, File для файла и т.д.)
 */
public interface ParserParcelProcessor<T> {

    /**
     * Возвращает список посылок из источника.
     *
     * @param source источник данных (не может быть null)
     * @return результат парсинга в виде ParserParcelProcessorResult - списка посылок и ошибок (не может быть null)
     */
    @NonNull
    ParserParcelProcessorResult transform(@NonNull T source);
}
