package ru.hofftech.shared.service.parser;

import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.ParserMachineProcessorResult;

/**
 * Интерфейс для получения списка машин.
 *
 * @param <T> тип источника данных (String для строки, File для файла и т.д.)
 */
public interface ParserMachineProcessor<T> {

    /**
     * Возвращает список машин из источника.
     *
     * @param source источник данных (не может быть null)
     * @return результат парсинга в виде ParserMachineProcessorResult - списка машин и ошибок (не может быть null)
     */
    @NonNull
    ParserMachineProcessorResult transform(@NonNull T source);
}
