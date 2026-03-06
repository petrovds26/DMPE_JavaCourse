package ru.hofftech.importmachine.service.output.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.importmachine.model.core.ImportMachineResult;
import ru.hofftech.importmachine.model.dto.ImportMachineOutputResultDto;
import ru.hofftech.importmachine.service.output.ImportMachineOutput;
import ru.hofftech.importmachine.util.ImportMachineMapperUtil;
import ru.hofftech.shared.model.enums.FileType;
import ru.hofftech.shared.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Вывод результата разгрузки машины в JSON файл
 */
@Slf4j
@RequiredArgsConstructor
public class ImportMachineOutputJson implements ImportMachineOutput {

    @Override
    public void output(ImportMachineResult result, String outputFilePath) {
        if (result == null) {
            log.error("Нельзя сохранить null результат");
            return;
        }

        if (outputFilePath == null || outputFilePath.isBlank()) {
            log.error("Не указан путь для сохранения JSON файла");
            return;
        }

        try {
            // Преобразуем результат в DTO для вывода
            ImportMachineOutputResultDto outputDto = ImportMachineMapperUtil.loadingResultToOutputDto(result);

            // Сериализуем в JSON
            String json = JsonUtil.toJson(outputDto);

            // Сохраняем в файл
            Files.writeString(Path.of(outputFilePath), json);

            log.info("Результат успешно сохранён в JSON файл: {}", outputFilePath);

        } catch (IOException e) {
            log.error("Ошибка при сохранении результата в JSON файл {}: {}", outputFilePath, e.getMessage(), e);
        }
    }

    @Override
    public Optional<FileType> getFileTypeOptional() {
        return Optional.of(FileType.JSON);
    }

    @Override
    public String getDescription() {
        return FileType.JSON.getDescription();
    }
}
