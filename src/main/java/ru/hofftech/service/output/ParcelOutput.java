package ru.hofftech.service.output;

import ru.hofftech.model.dto.LoadingResult;

public interface ParcelOutput {
    /**
     * Выводит результат упаковки
     * @param result результат упаковки
     */
    void print(LoadingResult result);
}
