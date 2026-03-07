package ru.hofftech.importparcel.service.parser.parcel.source.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.importparcel.model.dto.ImportParcelDto;
import ru.hofftech.importparcel.service.parser.parcel.source.ImportParcelFileSource;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.enums.FileType;
import ru.hofftech.shared.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Источник данных из JSON файла
 */
@Slf4j
@RequiredArgsConstructor
public class ImportParcelJsonFileSource implements ImportParcelFileSource<String> {

    @Override
    public @NonNull List<List<String>> getParcelBlocks(@NonNull String filePath) throws IOException {
        log.debug("Чтение JSON файла: {}", filePath);

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + filePath);
        }

        // Читаем содержимое файла
        String jsonContent = Files.readString(path);

        ImportParcelDto importParcelDto = JsonUtil.fromJson(jsonContent, ImportParcelDto.class);

        // Преобразуем ParcelDto в список строк для совместимости с существующей системой
        List<List<String>> blocks = convertToParcelBlocks(importParcelDto);

        log.debug("Файл прочитан. Найдено блоков (посылок): {}", blocks.size());

        return blocks;
    }

    @Override
    public @NonNull FileType getFileType() {
        return FileType.JSON;
    }

    /**
     * Преобразует строку с \n в список строк
     */
    @NonNull
    private List<String> splitDisplay(@NonNull String display) {
        return Arrays.asList(display.split("\n"));
    }

    @NonNull
    private List<List<String>> convertToParcelBlocks(@NonNull ImportParcelDto importParcelDto) {
        return importParcelDto.parcels().stream()
                .map(ParcelDto::form)
                .map(this::splitDisplay)
                .toList();
    }
}
