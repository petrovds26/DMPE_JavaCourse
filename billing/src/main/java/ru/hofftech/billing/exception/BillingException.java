package ru.hofftech.billing.exception;

import lombok.experimental.StandardException;
import org.jspecify.annotations.NullMarked;

/**
 * Исключение, выбрасываемое при ошибках в сервисе биллинга.
 * <p>
 * Возникает в следующих случаях:
 * <ul>
 *   <li>Попытка создания дублирующей записи с существующим externalId</li>
 *   <li>Ошибки при обработке сообщений из Kafka</li>
 *   <li>Ошибки валидации данных биллинга</li>
 * </ul>
 */
@NullMarked
@StandardException
public class BillingException extends RuntimeException {

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
