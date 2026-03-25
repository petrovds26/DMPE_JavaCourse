package ru.hofftech.unload.service.output;

import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.unload.model.core.UnloadResult;

@NullMarked
public interface UnloadPrepareOutputResult {
    /**
     * Выводит результат упаковки
     *
     * @param result результат упаковки
     */
    ProcessorCommandResult output(UnloadResult result);
}
