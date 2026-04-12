package ru.hofftech.core.exception;

import lombok.experimental.StandardException;
import org.jspecify.annotations.NullMarked;

/**
 * Исключение, выбрасываемое при ошибках работы с JSON.
 * <p>
 * Возникает при сериализации или десериализации JSON данных.
 */
@NullMarked
@StandardException
public class JsonUtilException extends RuntimeException {

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
