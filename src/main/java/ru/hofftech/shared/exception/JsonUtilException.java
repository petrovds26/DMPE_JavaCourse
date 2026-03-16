package ru.hofftech.shared.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jspecify.annotations.NonNull;

/**
 * Исключение, выбрасываемое при ошибках работы с JSON.
 * Оборачивает {@link JsonProcessingException} в непроверяемое исключение.
 */
public class JsonUtilException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message сообщение об ошибке (не может быть null)
     * @param cause   причина исключения (не может быть null)
     */
    public JsonUtilException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
