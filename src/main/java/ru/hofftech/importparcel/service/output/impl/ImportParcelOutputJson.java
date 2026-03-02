package ru.hofftech.importparcel.service.output.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.model.dto.ImportParcelOutputResultDto;
import ru.hofftech.importparcel.service.output.ImportParcelOutput;
import ru.hofftech.importparcel.util.ImportParcelMapperUtil;
import ru.hofftech.shared.model.enums.FileType;
import ru.hofftech.shared.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Вывод результата упаковки в JSON файл
 */
@Slf4j
@RequiredArgsConstructor
public class ImportParcelOutputJson implements ImportParcelOutput {

    @Override
    public void output(ImportParcelResult result, String outputFilePath) {
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
            ImportParcelOutputResultDto outputDto = ImportParcelMapperUtil.loadingResultToOutputDto(result);

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
    public FileType getFileType() {
        return FileType.JSON;
    }

    @Override
    public String getDescription() {
        return FileType.JSON.getDescription();
    }
}
