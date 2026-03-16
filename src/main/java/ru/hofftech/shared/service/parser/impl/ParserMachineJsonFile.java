package ru.hofftech.shared.service.parser.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.ParserMachineProcessorResult;
import ru.hofftech.shared.model.dto.MachinesDto;
import ru.hofftech.shared.service.parser.ParserMachineProcessor;
import ru.hofftech.shared.util.JsonUtil;
import ru.hofftech.shared.util.MapperUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Парсер для получения заполненных машин из JSON файла.
 * Формат файла: {"parcelsName": ["название1", "название2", ...]}
 */
@Slf4j
@RequiredArgsConstructor
public class ParserMachineJsonFile implements ParserMachineProcessor<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull ParserMachineProcessorResult transform(@NonNull String inputJsonFileName) {
        List<Machine> machines = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        log.debug("Чтение JSON файла: {}", inputJsonFileName);

        Path path = Path.of(inputJsonFileName);
        if (!Files.exists(path)) {
            String noFileError = String.format("Файл не найден: %s", inputJsonFileName);
            log.debug(noFileError);
            errors.add(noFileError);
            return ParserMachineProcessorResult.builder()
                    .machines(machines)
                    .errors(errors)
                    .build();
        }
        try {
            // Читаем содержимое файла
            String jsonContent = Files.readString(path);

            MachinesDto machinesDto = JsonUtil.fromJson(jsonContent, MachinesDto.class);

            // Преобразуем ParcelDto в список строк для совместимости с существующей системой
            machines = MapperUtil.dtoToMachines(machinesDto.machines().stream().toList());

            log.debug("Файл прочитан. Найдено машин: {}", machines.size());
        } catch (IOException e) {
            String readFileError =
                    String.format("Ошибка при открытии и чтении файла: %s, %s", inputJsonFileName, e.getMessage());
            log.debug(readFileError);
            errors.add(readFileError);
            return ParserMachineProcessorResult.builder()
                    .machines(machines)
                    .errors(errors)
                    .build();
        }

        return ParserMachineProcessorResult.builder()
                .machines(machines)
                .errors(errors)
                .build();
    }
}
