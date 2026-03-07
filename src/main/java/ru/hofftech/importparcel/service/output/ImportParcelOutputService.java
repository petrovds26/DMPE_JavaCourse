package ru.hofftech.importparcel.service.output;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importparcel.service.output.impl.ImportParcelOutputJson;
import ru.hofftech.importparcel.service.output.impl.ImportParcelOutputLog;
import ru.hofftech.shared.model.enums.FileType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис для выбора подходящего вывода результатов упаковки посылок.
 * Позволяет получить реализацию {@link ImportParcelOutput} по типу файла.
 */
@Slf4j
public class ImportParcelOutputService {
    @NonNull
    private final List<ImportParcelOutput> importParcelOutput;

    @NonNull
    private final ImportParcelOutput importParcelOutputLog;

    /**
     * Конструктор, инициализирующий все доступные реализации вывода.
     */
    public ImportParcelOutputService() {
        this.importParcelOutput = List.of(new ImportParcelOutputJson());
        this.importParcelOutputLog = new ImportParcelOutputLog();
    }

    /**
     * Возвращает подходящий вывод по типу файла.
     * Если тип файла не указан, возвращает вывод в лог.
     *
     * @param fileType тип файла (может быть null)
     * @return реализация {@link ImportParcelOutput} или null, если тип не поддерживается
     */
    @Nullable
    public ImportParcelOutput getOutputByFileType(@Nullable FileType fileType) {
        if (fileType == null) {
            return importParcelOutputLog;
        }
        return importParcelOutput.stream()
                .filter(source -> Objects.equals(source.getFileType(), fileType))
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает описание всех поддерживаемых типов файлов.
     *
     * @return строка с описанием форматов (например, "json - JSON файл")
     */
    @NonNull
    public String getAvailableFileExtensionDescription() {
        return importParcelOutput.stream()
                .filter(output -> output.getFileType() != null)
                .map(output -> String.format(
                        "%s - %s",
                        output.getFileType().getExtension(),
                        output.getFileType().getDescription()))
                .collect(Collectors.joining("; "));
    }
}
