package ru.hofftech.importmachine.service.output;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importmachine.model.core.ImportMachineResult;
import ru.hofftech.shared.model.enums.FileType;

import java.util.Optional;

public interface ImportMachineOutput {
    /**
     * Выводит результат разгрузки машин
     * @param result результат разгрузки машин
     * @param outputPath путь для вывода (может быть null для лога)
     */
    void output(@NonNull ImportMachineResult result, @Nullable String outputPath);

    /**
     * @return тип файла, который поддерживает этот вывод (null для лога)
     */
    @NonNull
    Optional<FileType> getFileTypeOptional();

    /**
     * @return описание вывода
     */
    @NonNull
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}
