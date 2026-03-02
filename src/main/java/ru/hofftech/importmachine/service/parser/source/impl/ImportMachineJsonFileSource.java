package ru.hofftech.importmachine.service.parser.source.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.importmachine.model.dto.ImportMachineDto;
import ru.hofftech.importmachine.service.parser.source.ImportMachineFileSource;
import ru.hofftech.shared.model.dto.MachineDto;
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
public class ImportMachineJsonFileSource implements ImportMachineFileSource<String> {

    @Override
    public List<MachineDto> getMachines(String filePath) throws IOException {
        log.debug("Чтение JSON файла: {}", filePath);

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + filePath);
        }

        // Читаем содержимое файла
        String jsonContent = Files.readString(path);

        ImportMachineDto importMachineDto = JsonUtil.fromJson(jsonContent, ImportMachineDto.class);

        // Преобразуем ParcelDto в список строк для совместимости с существующей системой
        List<MachineDto> machines = convertToMachines(importMachineDto);

        log.debug("Файл прочитан. Найдено машин: {}", machines.size());

        return machines;
    }

    @Override
    public FileType getFileType() {
        return FileType.JSON;
    }

    private List<MachineDto> convertToMachines(ImportMachineDto importMachineDto) {
        return importMachineDto.machines().stream().toList();
    }
}
