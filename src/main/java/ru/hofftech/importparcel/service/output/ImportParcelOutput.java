package ru.hofftech.importparcel.service.output;

import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.shared.model.enums.FileType;

public interface ImportParcelOutput {
    /**
     * Выводит результат упаковки
     * @param result результат упаковки
     */
    void output(ImportParcelResult result, String outputPath);

    /**
     * @return тип файла, который поддерживает этот вывод (null для лога)
     */
    FileType getFileType();

    /**
     * @return описание вывода
     */
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}
