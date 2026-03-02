package ru.hofftech.importmachine.service.output;

import ru.hofftech.importmachine.model.core.ImportMachineResult;
import ru.hofftech.shared.model.enums.FileType;

public interface ImportMachineOutput {
    /**
     * Выводит результат разгрузки машин
     * @param result результат разгрузки машин
     * @param outputPath путь для вывода (может быть null для лога)
     */
    void output(ImportMachineResult result, String outputPath);

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
