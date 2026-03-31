package ru.hofftech.load.service.output;

import org.jspecify.annotations.NullMarked;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.shared.model.core.ProcessorCommandResult;

@NullMarked
public interface LoadPrepareOutputResult {
    /**
     * Выводит результат упаковки
     *
     * @param result результат упаковки
     */
    ProcessorCommandResult output(LoadResult result);
}
