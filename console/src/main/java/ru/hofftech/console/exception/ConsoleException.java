package ru.hofftech.console.exception;

import lombok.experimental.StandardException;
import org.jspecify.annotations.NullMarked;

/**
 * Исключение, выбрасываемое при ошибках в консольном приложении.
 * <p>
 * Возникает при ошибках работы с файловой системой или других
 * операциях, специфичных для консольного модуля.
 */
@NullMarked
@StandardException
public class ConsoleException extends RuntimeException {

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
