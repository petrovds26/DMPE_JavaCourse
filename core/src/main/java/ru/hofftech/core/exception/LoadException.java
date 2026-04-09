package ru.hofftech.core.exception;

import lombok.experimental.StandardException;
import org.jspecify.annotations.NullMarked;

/**
 * Исключение, выбрасываемое при ошибках в процессе загрузки посылок в машины.
 * <p>
 * Возникает в следующих случаях:
 * <ul>
 *   <li>Не найдено ни одной машины для загрузки</li>
 *   <li>Не найдена стратегия загрузки</li>
 *   <li>Ошибки при парсинге машин или посылок</li>
 * </ul>
 */
@NullMarked
@StandardException
public class LoadException extends RuntimeException {
    /**
     * Подавляет заполнение стека для улучшения производительности.
     *
     * @return this
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
