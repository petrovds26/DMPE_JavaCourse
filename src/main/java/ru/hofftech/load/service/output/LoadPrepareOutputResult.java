package ru.hofftech.load.service.output;

import org.jspecify.annotations.NonNull;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.shared.model.core.ProcessorCommandResult;

public interface LoadPrepareOutputResult {
    /**
     * Выводит результат упаковки
     *
     * @param result результат упаковки
     */
    ProcessorCommandResult output(@NonNull LoadResult result);
}
