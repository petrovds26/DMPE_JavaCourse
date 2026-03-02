package ru.hofftech.importparcel.service.parser.parcel.source.impl;

import lombok.RequiredArgsConstructor;
import ru.hofftech.importparcel.service.parser.parcel.source.ImportParcelSource;

import java.util.List;

/**
 * Источник данных из строки (для тестов)
 */
@RequiredArgsConstructor
public class ImportParcelStringSource implements ImportParcelSource<List<List<String>>> {

    @Override
    public List<List<String>> getParcelBlocks(List<List<String>> blockSource) {

        return blockSource;
    }
}
