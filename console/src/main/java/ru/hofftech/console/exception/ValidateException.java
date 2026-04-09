package ru.hofftech.console.exception;

import lombok.experimental.StandardException;
import org.jspecify.annotations.NullMarked;

/**
 * Исключение, выбрасываемое при ошибках валидации входных данных.
 * <p>
 * Возникает при проверке параметров команд, файлов, дат и других входных данных.
 */
@NullMarked
@StandardException
public class ValidateException extends RuntimeException {

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
