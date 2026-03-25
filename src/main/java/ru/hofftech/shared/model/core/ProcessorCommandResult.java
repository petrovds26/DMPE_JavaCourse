package ru.hofftech.shared.model.core;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

/**
 * Результат выполнения процессорной команды.
 *
 * @param success успешность выполнения
 * @param message сообщение о результате
 */
@NullMarked
@Builder
public record ProcessorCommandResult(boolean success, String message) {
    /**
     * Создаёт успешный результат выполнения команды.
     *
     * @param message сообщение о результате (не может быть null)
     * @return успешный результат (не может быть null)
     */
    public static ProcessorCommandResult createSuccess(String message) {
        return ProcessorCommandResult.builder().success(true).message(message).build();
    }

    /**
     * Создаёт неуспешный результат выполнения команды.
     *
     * @param message сообщение об ошибке (не может быть null)
     * @return неуспешный результат (не может быть null)
     */
    public static ProcessorCommandResult createFailure(String message) {
        return ProcessorCommandResult.builder().success(false).message(message).build();
    }
}
