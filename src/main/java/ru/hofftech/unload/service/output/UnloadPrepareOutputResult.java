package ru.hofftech.unload.service.output;

import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.unload.model.core.UnloadResult;

public interface UnloadPrepareOutputResult {
    /**
     * Выводит результат упаковки
     *
     * @param result результат упаковки
     */
    ProcessorCommandResult output(@NonNull UnloadResult result);
}
