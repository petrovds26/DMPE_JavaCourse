package ru.hofftech.telegram.exception;

import lombok.experimental.StandardException;
import org.jspecify.annotations.NullMarked;

/**
 * Исключение, выбрасываемое при ошибках валидации входных данных.
 */
@StandardException
@NullMarked
public class ValidateException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
