package ru.hofftech.importparcel.service.output;

import lombok.extern.slf4j.Slf4j;
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
    private final List<ImportParcelOutput> importParcelOutput;
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
    public ImportParcelOutput getOutputByFileType(FileType fileType) {
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
    public String getAvailableFileExtensionDescription() {
        return importParcelOutput.stream()
                .map(fileType -> String.format(
                        "%s - %s",
                        fileType.getFileType().getExtension(),
                        fileType.getFileType().getDescription()))
                .collect(Collectors.joining("; "));
    }
}
