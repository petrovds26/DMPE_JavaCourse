package ru.hofftech.shared.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hofftech.shared.model.enums.ResponseCode;

/**
 * Стандартный объект ответа API.
 * <p>
 * Оборачивает все ответы API в единый формат, содержащий метаданные результата
 * и полезную нагрузку. Используется во всех REST контроллерах для унификации ответов.
 *
 * @param <T> тип данных полезной нагрузки
 */
@Data
@Valid
@Builder
@NoArgsConstructor
public class Response<T> {

    private static final int RESULT_TYPE_POSITION = 1;
    private static final char SUCCESS = '0';

    @NotNull
    @Schema(
            description = "Расширенная информация по статусу обработки запроса",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private @Valid ResponseMetaData result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Полезная нагрузка ответа")
    private T data;

    /**
     * Конструктор с метаданными и данными.
     *
     * @param result метаданные ответа
     * @param data   полезная нагрузка
     */
    public Response(@NotNull ResponseMetaData result, T data) {
        this.result = result;
        this.data = data;
    }

    /**
     * Конструктор только с метаданными.
     *
     * @param result метаданные ответа
     */
    public Response(@NotNull ResponseMetaData result) {
        this.result = result;
    }

    /**
     * Конструктор с кодом ответа.
     *
     * @param responseCode код ответа
     */
    public Response(@NotNull ResponseCode responseCode) {
        this.result = new ResponseMetaData(responseCode);
    }

    /**
     * Конструктор с кодом ответа и данными.
     *
     * @param responseCode код ответа
     * @param data         полезная нагрузка
     */
    public Response(@NotNull ResponseCode responseCode, T data) {
        this.result = new ResponseMetaData(responseCode);
        this.data = data;
    }

    /**
     * Создаёт успешный ответ с данными.
     *
     * @param data полезная нагрузка
     * @param <T>  тип данных
     * @return успешный ответ
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(ResponseCode.OK, data);
    }

    /**
     * Создаёт успешный ответ без данных.
     *
     * @param <T> тип данных
     * @return успешный ответ
     */
    public static <T> Response<T> success() {
        return new Response<>(ResponseCode.OK);
    }

    /**
     * Создаёт ответ с ошибкой по коду.
     *
     * @param responseCode код ошибки
     * @param <T>          тип данных
     * @return ответ с ошибкой
     */
    public static <T> Response<T> error(ResponseCode responseCode) {
        return new Response<>(responseCode);
    }

    /**
     * Создаёт ответ с ошибкой и пользовательским сообщением.
     *
     * @param responseCode   код ошибки
     * @param customMessage пользовательское сообщение
     * @param <T>            тип данных
     * @return ответ с ошибкой
     */
    public static <T> Response<T> error(ResponseCode responseCode, String customMessage) {
        ResponseMetaData meta = new ResponseMetaData(responseCode);
        meta.setMessage(customMessage);
        return new Response<>(meta);
    }

    /**
     * Проверяет, является ли ответ ошибочным.
     *
     * @return true если ответ содержит ошибку
     */
    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * Проверяет, является ли ответ успешным.
     *
     * @return true если ответ успешен
     */
    @JsonIgnore
    public boolean isSuccess() {
        return result != null
                && result.getCode().length() > RESULT_TYPE_POSITION
                && SUCCESS == result.getCode().charAt(RESULT_TYPE_POSITION);
    }
}
