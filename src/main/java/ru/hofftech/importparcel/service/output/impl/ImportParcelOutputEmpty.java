package ru.hofftech.importparcel.service.output.impl;

import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.service.output.ImportParcelOutput;
import ru.hofftech.shared.model.enums.FileType;

/**
 * Класс, который позволяет ничего не делать с результатом.
 */
public class ImportParcelOutputEmpty implements ImportParcelOutput {
    @Override
    public void output(ImportParcelResult result, String fileName) {
        // do nothing
    }

    @Override
    public FileType getFileType() {
        return null;
    }
}
