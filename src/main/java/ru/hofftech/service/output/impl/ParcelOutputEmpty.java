package ru.hofftech.service.output.impl;

import ru.hofftech.model.dto.LoadingResult;
import ru.hofftech.service.output.ParcelOutput;

/**
 * Класс, который позволяет ничего не делать с результатом.
 */
public class ParcelOutputEmpty implements ParcelOutput {
    @Override
    public void print(LoadingResult result) {
        // do nothing
    }
}
