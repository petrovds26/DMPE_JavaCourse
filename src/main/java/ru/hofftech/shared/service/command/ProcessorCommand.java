package ru.hofftech.shared.service.command;

import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.ProcessorCommandResult;

/**
 * Интерфейс для процессорных команд (бизнес-логика).
 */
@NullMarked
public interface ProcessorCommand<T> {

    /**
     * Выполняет бизнес-логику команды.
     *
     * @return результат выполнения
     */
    ProcessorCommandResult execute(T source);
}
