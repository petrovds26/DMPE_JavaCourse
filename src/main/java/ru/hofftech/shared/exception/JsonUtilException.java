package ru.hofftech.shared.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jspecify.annotations.NullMarked;

/**
 * Исключение, выбрасываемое при ошибках работы с JSON.
 * Оборачивает {@link JsonProcessingException} в непроверяемое исключение.
 */
@NullMarked
public class JsonUtilException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message сообщение об ошибке (не может быть null)
     * @param cause   причина исключения (не может быть null)
     */
    public JsonUtilException(String message, Throwable cause) {
        super(message, cause);
    }
}
