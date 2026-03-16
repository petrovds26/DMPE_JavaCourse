package ru.hofftech.shared.service.command;

import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.ProcessorCommandResult;

/**
 * Интерфейс для процессорных команд (бизнес-логика).
 */
public interface ProcessorCommand<T> {

    /**
     * Выполняет бизнес-логику команды.
     *
     * @return результат выполнения
     */
    @NonNull
    ProcessorCommandResult execute(T source);
}
