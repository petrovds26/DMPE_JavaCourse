package ru.hofftech.service.parser.source.impl;

import lombok.RequiredArgsConstructor;
import ru.hofftech.service.parser.source.ParcelSource;

import java.util.List;

/**
 * Источник данных из строки (для тестов)
 */
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class StringParcelSource implements ParcelSource {

    private final List<List<String>> blockSource;

    @Override
    public List<List<String>> getParcelBlocks() {

        return blockSource;
    }
}
