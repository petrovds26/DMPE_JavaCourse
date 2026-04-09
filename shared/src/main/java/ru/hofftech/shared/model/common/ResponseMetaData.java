package ru.hofftech.shared.model.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.enums.ResponseCode;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Метаданные ответа API.
 * Содержит временную метку, HTTP статус, код результата и описание.
 */
@Data
@Valid
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NullMarked
public class ResponseMetaData {

    @Schema(description = "Дата и время", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime timestamp;

    @Min(value = 100)
    @Max(value = 527)
    @Schema(description = "Http статус ответа", requiredMode = Schema.RequiredMode.REQUIRED)
    private int status;

    @NotBlank
    @Size(max = 10)
    @Schema(description = "Код результата обработки запроса", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank
    @Schema(description = "Описание кода обработки запроса", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    /**
     * Конструктор на основе кода ответа.
     *
     * @param responseCode код ответа
     */
    public ResponseMetaData(ResponseCode responseCode) {
        this.timestamp = OffsetDateTime.now(ZoneOffset.UTC);
        this.status = responseCode.getStatus();
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    /**
     * Конструктор с явным указанием всех полей.
     *
     * @param status  HTTP статус
     * @param code    код результата
     * @param message сообщение
     */
    public ResponseMetaData(int status, String code, String message) {
        this.timestamp = OffsetDateTime.now(ZoneOffset.UTC);
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
