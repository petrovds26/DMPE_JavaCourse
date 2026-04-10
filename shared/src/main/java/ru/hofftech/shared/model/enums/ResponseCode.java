package ru.hofftech.shared.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * Коды ответов API.
 * <p>
 * Определяет стандартные коды ответов для HTTP API с
 * внутренними кодами для детализации ошибок.
 */
@Getter
@RequiredArgsConstructor
@NullMarked
public enum ResponseCode {
    OK(200, "00000", "Успешный ответ"),
    BAD_REQUEST(400, "01003", "Некорректный запрос"),
    REQUEST_TIMEOUT(408, "02000", "Превышено время ожидания ответа"),
    INTERNAL_SERVER_ERROR(500, "02001", "Внутренняя ошибка сервера");

    private final int status;
    private final String code;
    private final String message;
}
