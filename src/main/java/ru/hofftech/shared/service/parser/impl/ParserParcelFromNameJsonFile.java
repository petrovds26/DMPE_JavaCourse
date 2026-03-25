package ru.hofftech.shared.service.parser.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.ParserParcelProcessorResult;
import ru.hofftech.shared.model.dto.ParcelsNameDto;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.parser.ParserParcelProcessor;
import ru.hofftech.shared.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Парсер для получения посылок из JSON файла с названиями.
 * Формат файла: {"parcelsName": ["название1", "название2", ...]}
 */
@Slf4j
@RequiredArgsConstructor
@NullMarked
@SuppressWarnings("ClassCanBeRecord")
public class ParserParcelFromNameJsonFile implements ParserParcelProcessor<String> {

    public final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ParserParcelProcessorResult transform(String inputJsonFileName) {
        List<Parcel> parcels = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        log.debug("Чтение JSON файла: {}", inputJsonFileName);

        Path path = Path.of(inputJsonFileName);
        if (!Files.exists(path)) {
            String noFileError = String.format("Файл не найден: %s", inputJsonFileName);
            log.debug(noFileError);
            errors.add(noFileError);
            return ParserParcelProcessorResult.builder()
                    .parcels(parcels)
                    .errors(errors)
                    .build();
        }
        try {
            // Читаем содержимое файла
            String jsonContent = Files.readString(path);

            ParcelsNameDto parcelsNameDto = JsonUtil.fromJson(jsonContent, ParcelsNameDto.class);

            // Для каждой строки распознаем название посылки и попытаемся найти в репозитории
            for (String parcelName : parcelsNameDto.parcelsName()) {
                parcelRepository
                        .find(parcelName)
                        .ifPresentOrElse(parcels::add, () -> errors.add("Не найдена посылка с именем " + parcelName));
            }
        } catch (IOException e) {
            String readFileError =
                    String.format("Ошибка при открытии и чтении файла: %s, %s", inputJsonFileName, e.getMessage());
            log.debug(readFileError);
            errors.add(readFileError);
            return ParserParcelProcessorResult.builder()
                    .parcels(parcels)
                    .errors(errors)
                    .build();
        }

        return ParserParcelProcessorResult.builder()
                .parcels(parcels)
                .errors(errors)
                .build();
    }
}
