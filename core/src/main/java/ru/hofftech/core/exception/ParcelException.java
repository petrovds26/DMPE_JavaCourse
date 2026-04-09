package ru.hofftech.core.exception;

import lombok.experimental.StandardException;
import org.jspecify.annotations.NullMarked;

/**
 * Исключение, выбрасываемое при ошибках работы с посылками.
 * <p>
 * Возникает в следующих случаях:
 * <ul>
 *   <li>Посылка с таким названием уже существует</li>
 *   <li>Посылка с таким названием не найдена</li>
 *   <li>Не удалось распознать форму посылки</li>
 * </ul>
 */
@NullMarked
@StandardException
public class ParcelException extends RuntimeException {

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
