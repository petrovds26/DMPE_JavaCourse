package ru.hofftech.shared.exception;

import org.jspecify.annotations.NonNull;

public class JsonUtilException extends RuntimeException {
    public JsonUtilException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
