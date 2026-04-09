package ru.hofftech.console.exception;

import lombok.experimental.StandardException;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.console.model.constant.InformationMessage;
import ru.hofftech.shared.model.common.Response;

/**
 * Исключение, выбрасываемое при ошибках взаимодействия с Core сервисом через Feign.
 */
@NullMarked
@StandardException
public class FeignException extends RuntimeException {

    /**
     * Создаёт исключение на основе ответа от Core сервиса.
     *
     * @param response ответ от сервера с информацией об ошибке
     */
    public FeignException(Response<?> response) {
        super(InformationMessage.FEIGN_ERROR.formatted(
                response.getResult().getMessage(), response.getResult().getCode()));
    }

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
