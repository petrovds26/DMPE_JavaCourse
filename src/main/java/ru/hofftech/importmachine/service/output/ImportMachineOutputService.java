package ru.hofftech.importmachine.service.output;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.importmachine.service.output.impl.ImportMachineOutputJson;
import ru.hofftech.importmachine.service.output.impl.ImportMachineOutputLog;
import ru.hofftech.importmachine.service.output.impl.ImportMachineOutputTxt;
import ru.hofftech.shared.model.enums.FileType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис для выбора подходящего вывода результатов разгрузки машин.
 * Позволяет получить реализацию {@link ImportMachineOutput} по типу файла.
 */
@Slf4j
public class ImportMachineOutputService {
    private final List<ImportMachineOutput> importMachineOutput;
    private final ImportMachineOutput importMachineOutputLog;

    /**
     * Конструктор, инициализирующий все доступные реализации вывода.
     */
    public ImportMachineOutputService() {
        this.importMachineOutput = List.of(new ImportMachineOutputJson(), new ImportMachineOutputTxt());
        this.importMachineOutputLog = new ImportMachineOutputLog();
    }

    /**
     * Возвращает подходящий вывод по типу файла.
     * Если тип файла не указан, возвращает вывод в лог.
     *
     * @param fileType тип файла (может быть null)
     * @return реализация {@link ImportMachineOutput} или null, если тип не поддерживается
     */
    public ImportMachineOutput getOutputByFileType(FileType fileType) {
        if (fileType == null) {
            return importMachineOutputLog;
        }
        return importMachineOutput.stream()
                .filter(source -> Objects.equals(source.getFileType(), fileType))
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает описание всех поддерживаемых типов файлов.
     *
     * @return строка с описанием форматов (например, "json - JSON файл; txt - Текстовый файл")
     */
    public String getAvailableFileExtensionDescription() {
        return importMachineOutput.stream()
                .map(fileType -> String.format(
                        "%s - %s",
                        fileType.getFileType().getExtension(),
                        fileType.getFileType().getDescription()))
                .collect(Collectors.joining("; "));
    }
}
