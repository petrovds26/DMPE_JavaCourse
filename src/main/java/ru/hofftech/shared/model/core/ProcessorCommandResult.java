package ru.hofftech.shared.model.core;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

/**
 * Результат выполнения процессорной команды.
 *
 * @param success успешность выполнения
 * @param message сообщение о результате
 */
@Builder
public record ProcessorCommandResult(boolean success, @NonNull String message) {}
