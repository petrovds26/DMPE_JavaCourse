package ru.hofftech.importmachine.service.output;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importmachine.service.output.impl.ImportMachineOutputJson;
import ru.hofftech.importmachine.service.output.impl.ImportMachineOutputLog;
import ru.hofftech.importmachine.service.output.impl.ImportMachineOutputTxt;
import ru.hofftech.shared.model.enums.FileType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для выбора подходящего вывода результатов разгрузки машин.
 * Позволяет получить реализацию {@link ImportMachineOutput} по типу файла.
 */
@Slf4j
public class ImportMachineOutputService {
    @NonNull
    private final List<ImportMachineOutput> importMachineOutput;

    /**
     * Конструктор, инициализирующий все доступные реализации вывода.
     */
    public ImportMachineOutputService() {
        this.importMachineOutput =
                List.of(new ImportMachineOutputJson(), new ImportMachineOutputTxt(), new ImportMachineOutputLog());
    }

    /**
     * Возвращает подходящий вывод по типу файла.
     * Если тип файла не указан, возвращает вывод в лог.
     *
     * @param fileType тип файла (может быть null)
     * @return {@link Optional} с реализацией {@link ImportMachineOutput},
     *         пустой Optional, если тип не поддерживается
     */
    @NonNull
    public Optional<ImportMachineOutput> getOutputByFileType(@Nullable FileType fileType) {
        if (fileType == null) {
            return importMachineOutput.stream()
                    .filter(source -> source.getFileType().isEmpty())
                    .findFirst();
        }
        return importMachineOutput.stream()
                .filter(source ->
                        source.getFileType().map(type -> type == fileType).orElse(false))
                .findFirst();
    }

    /**
     * Возвращает описание всех поддерживаемых типов файлов.
     *
     * @return строка с описанием форматов (например, "json - JSON файл; txt - Текстовый файл")
     */
    @NonNull
    public String getAvailableFileExtensionDescription() {
        return importMachineOutput.stream()
                .map(output -> String.format(
                        "%s - %s",
                        output.getFileType().isEmpty()
                                ? "[пусто]"
                                : output.getFileType().get().getExtension(),
                        output.getDescription()))
                .collect(Collectors.joining("; "));
    }
}
