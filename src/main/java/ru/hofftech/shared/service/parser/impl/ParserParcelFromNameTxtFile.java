package ru.hofftech.shared.service.parser.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.ParserParcelProcessorResult;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.parser.ParserParcelProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Парсер для получения посылок из текстового файла с названиями.
 * Файл содержит названия посылок, каждое на новой строке.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class ParserParcelFromNameTxtFile implements ParserParcelProcessor<String> {

    @NonNull
    public final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull ParserParcelProcessorResult transform(@NonNull String inputTxtFileName) {
        List<Parcel> parcels = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        log.debug("Чтение Txt файла: {}", inputTxtFileName);

        Path path = Path.of(inputTxtFileName);
        if (!Files.exists(path)) {
            String noFileError = String.format("Файл не найден: %s", inputTxtFileName);
            log.debug(noFileError);
            errors.add(noFileError);
            return ParserParcelProcessorResult.builder()
                    .parcels(parcels)
                    .errors(errors)
                    .build();
        }
        try {
            // Читаем содержимое файла
            List<String> allLines = Files.readAllLines(path);
            // Для каждой строки распознаем название посылки и попытаемся найти в репозитории
            for (String line : allLines) {
                if (!line.isBlank()) {
                    parcelRepository
                            .find(line)
                            .ifPresentOrElse(parcels::add, () -> errors.add("Не найдена посылка с именем " + line));
                }
            }
        } catch (IOException e) {
            String readFileError =
                    String.format("Ошибка при открытии и чтении файла: %s, %s", inputTxtFileName, e.getMessage());
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
