package ru.hofftech.importmachine.service.parser.source;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importmachine.service.parser.source.impl.ImportMachineJsonFileSource;
import ru.hofftech.shared.model.enums.FileType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис для выбора подходящего источника данных машин.
 * Позволяет получить реализацию {@link ImportMachineFileSource} по типу файла.
 */
@Slf4j
public class ImportMachineFileSourceService {
    private final List<ImportMachineFileSource<String>> fileMachineSources;

    /**
     * Конструктор, инициализирующий все доступные источники.
     */
    public ImportMachineFileSourceService() {
        this.fileMachineSources = List.of(new ImportMachineJsonFileSource());
    }

    /**
     * Возвращает подходящий источник данных по типу файла.
     *
     * @param fileType тип файла (например, FileType.JSON)
     * @return реализация {@link ImportMachineFileSource} или null, если тип не поддерживается
     */
    @Nullable
    public ImportMachineFileSource<String> getSourceByFileType(@NonNull FileType fileType) {
        return fileMachineSources.stream()
                .filter(source -> Objects.equals(source.getFileType(), fileType))
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает описание всех поддерживаемых типов файлов для машин.
     *
     * @return строка с описанием форматов (например, "json - JSON файл")
     */
    @NonNull
    public String getAvailableFileExtensionDescription() {
        return fileMachineSources.stream()
                .map(fileType -> String.format(
                        "%s - %s",
                        fileType.getFileType().getExtension(),
                        fileType.getFileType().getDescription()))
                .collect(Collectors.joining("; "));
    }
}
