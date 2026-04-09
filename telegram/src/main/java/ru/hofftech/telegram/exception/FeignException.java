package ru.hofftech.telegram.exception;

import lombok.experimental.StandardException;
import org.jspecify.annotations.NullMarked;

/**
 * Исключение, выбрасываемое при ошибках взаимодействия с Feign клиентом.
 */
@StandardException
@NullMarked
public class FeignException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
