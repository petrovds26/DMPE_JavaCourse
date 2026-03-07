package ru.hofftech.importparcel.service.parser.parcel.source;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importparcel.service.parser.parcel.source.impl.ImportParcelJsonFileSource;
import ru.hofftech.importparcel.service.parser.parcel.source.impl.ImportParcelTxtFileSource;
import ru.hofftech.shared.model.enums.FileType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис для выбора подходящего источника данных посылок.
 * Позволяет получить реализацию {@link ImportParcelFileSource} по типу файла.
 */
@Slf4j
public class ImportParcelFileSourceService {
    @NonNull
    private final List<ImportParcelFileSource<String>> fileParcelSources;

    /**
     * Конструктор, инициализирующий все доступные источники.
     */
    public ImportParcelFileSourceService() {
        this.fileParcelSources = List.of(new ImportParcelTxtFileSource(), new ImportParcelJsonFileSource());
    }

    /**
     * Возвращает подходящий источник данных по типу файла.
     *
     * @param fileType тип файла (например, FileType.TXT или FileType.JSON)
     * @return реализация {@link ImportParcelFileSource} или null, если тип не поддерживается
     */
    @Nullable
    public ImportParcelFileSource<String> getSourceByFileType(@NonNull FileType fileType) {
        return fileParcelSources.stream()
                .filter(source -> Objects.equals(source.getFileType(), fileType))
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает описание всех поддерживаемых типов файлов для посылок.
     *
     * @return строка с описанием форматов (например, "txt - Текстовый файл; json - JSON файл")
     */
    @NonNull
    public String getAvailableFileExtensionDescription() {
        return fileParcelSources.stream()
                .map(fileType -> String.format(
                        "%s - %s",
                        fileType.getFileType().getExtension(),
                        fileType.getFileType().getDescription()))
                .collect(Collectors.joining("; "));
    }
}
