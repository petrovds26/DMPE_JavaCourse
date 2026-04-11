package ru.hofftech.billing.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.enums.ResponseCode;

/**
 * Spring-обёртка для стандартного ответа Response.
 * <p>
 * Добавляет ResponseEntity к объекту Response для корректного HTTP-ответа.
 */
@NullMarked
@UtilityClass
public class ResponseWrapperUtil {

    /**
     * Оборачивает Response в ResponseEntity с соответствующим HTTP статусом.
     *
     * @param response объект Response
     * @param <T>      тип данных в Response
     * @return ResponseEntity с Response
     */
    public static <T> ResponseEntity<Response<T>> toResponseEntity(Response<T> response) {
        return ResponseEntity.status(response.getResult().getStatus()).body(response);
    }

    /**
     * Создаёт успешный ответ с данными.
     *
     * @param data данные для включения в ответ
     * @param <T>  тип данных
     * @return ResponseEntity с успешным ответом
     */
    public static <T> ResponseEntity<Response<T>> ok(T data) {
        return toResponseEntity(Response.success(data));
    }

    /**
     * Создаёт успешный ответ без данных.
     *
     * @param <T> тип данных
     * @return ResponseEntity с успешным ответом
     */
    public static <T> ResponseEntity<Response<T>> ok() {
        return toResponseEntity(Response.success());
    }

    /**
     * Создаёт ответ с ошибкой по коду.
     *
     * @param code код ошибки
     * @param <T>  тип данных
     * @return ResponseEntity с ответом об ошибке
     */
    public static <T> ResponseEntity<Response<T>> error(ResponseCode code) {
        return toResponseEntity(Response.error(code));
    }

    /**
     * Создаёт ответ с ошибкой и пользовательским сообщением.
     *
     * @param code    код ошибки
     * @param message пользовательское сообщение
     * @param <T>     тип данных
     * @return ResponseEntity с ответом об ошибке
     */
    public static <T> ResponseEntity<Response<T>> error(ResponseCode code, String message) {
        return toResponseEntity(Response.error(code, message));
    }
}
