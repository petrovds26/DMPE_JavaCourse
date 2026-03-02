package ru.hofftech.importmachine.service.output.impl;

import ru.hofftech.importmachine.model.core.ImportMachineResult;
import ru.hofftech.importmachine.service.output.ImportMachineOutput;
import ru.hofftech.shared.model.enums.FileType;

/**
 * Класс, который позволяет ничего не делать с результатом.
 */
public class ImportMachineOutputEmpty implements ImportMachineOutput {
    @Override
    public void output(ImportMachineResult result, String fileName) {
        // do nothing
    }

    @Override
    public FileType getFileType() {
        return null;
    }
}
