package ru.hofftech.importparcel.service.output;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.shared.model.enums.FileType;

public interface ImportParcelOutput {
    /**
     * Выводит результат упаковки
     * @param result результат упаковки
     */
    void output(@NonNull ImportParcelResult result, @Nullable String outputPath);

    /**
     * @return тип файла, который поддерживает этот вывод (null для лога)
     */
    @Nullable
    FileType getFileType();

    /**
     * @return описание вывода
     */
    @NonNull
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}
