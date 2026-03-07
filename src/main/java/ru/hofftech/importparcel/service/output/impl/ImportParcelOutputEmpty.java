package ru.hofftech.importparcel.service.output.impl;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.service.output.ImportParcelOutput;
import ru.hofftech.shared.model.enums.FileType;

/**
 * Класс, который позволяет ничего не делать с результатом.
 */
public class ImportParcelOutputEmpty implements ImportParcelOutput {
    @Override
    public void output(@NonNull ImportParcelResult result, @Nullable String fileName) {
        // do nothing
    }

    @Override
    public FileType getFileType() {
        return null;
    }
}
