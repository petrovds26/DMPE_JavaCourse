package ru.hofftech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.model.core.Parcel;
import ru.hofftech.model.dto.LoadingResult;
import ru.hofftech.service.loader.strategy.ParcelLoadingStrategy;
import ru.hofftech.service.output.ParcelOutput;
import ru.hofftech.service.parser.ParcelBuilder;
import ru.hofftech.service.parser.ParcelNormalizer;
import ru.hofftech.service.parser.source.ParcelSource;
import ru.hofftech.service.validation.impl.ParcelGridValidator;
import ru.hofftech.service.validation.impl.ParcelListStringValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Оркестратор - собирает всё вместе
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class ParcelProcessor {

    private final ParcelSource parcelSource;
    private final ParcelNormalizer normalizer;
    private final ParcelBuilder parcelBuilder;
    private final ParcelListStringValidator stringValidator;
    private final ParcelGridValidator gridValidator;
    private final ParcelLoadingStrategy loadingStrategy;
    private final ParcelOutput parcelOutput;

    public LoadingResult process() {
        try {
            List<Parcel> validParcels = new ArrayList<>();
            List<Parcel> invalidParcels = new ArrayList<>();
            List<Parcel> inputParcels = new ArrayList<>();

            // Шаг 1: получаем блоки строк из источника
            List<List<String>> blocks = parcelSource.getParcelBlocks();

            log.info("Начало обработки данных из {}", parcelSource.getDescription());

            // Шаг 2: обрабатываем каждый блок
            for (int i = 0; i < blocks.size(); i++) {
                List<String> rawLines = blocks.get(i);
                log.debug("Обработка посылки #{} ({} строк)", i + 1, rawLines.size());

                // Шаг 3: валидация сырых строк
                List<String> stringErrors = stringValidator.validate(rawLines);
                if (!stringErrors.isEmpty()) {
                    log.warn("Посылка #{} отклонена на этапе валидации строк. Ошибки: {}", i + 1, stringErrors);
                    continue;
                }

                // Шаг 4: нормализация
                List<String> normalizedLines = normalizer.normalize(rawLines);

                // Шаг 5: создание посылки
                Parcel parcel = parcelBuilder.buildFromLines(normalizedLines);
                inputParcels.add(parcel);

                // Шаг 6: валидация готовой посылки
                List<String> gridErrors = gridValidator.validate(parcel);

                if (gridErrors.isEmpty()) {
                    validParcels.add(parcel);
                    log.debug("Посылка #{} успешно загружена", i + 1);
                } else {
                    invalidParcels.add(parcel);
                    log.warn("Посылка #{} отклонена на этапе валидации grid. Ошибки: {}", i + 1, gridErrors);
                }
            }

            log.info("Обработка файла завершена. Загружено {}/{} посылок", validParcels.size(), blocks.size());

            // Шаг 7: запуск расчета загрузки машин
            log.debug(
                    "Начало упаковки {} посылок по алгоритму: {}",
                    validParcels.size(),
                    loadingStrategy.getAlgorithmName());

            LoadingResult packingResult = loadingStrategy.loadParcels(validParcels);

            LoadingResult extendedResult = LoadingResult.builder()
                    .machines(packingResult.machines())
                    .oversizedParcels(packingResult.oversizedParcels())
                    .invalidParcels(invalidParcels)
                    .inputParcels(inputParcels)
                    .build();

            // Шаг 9: выводим результат
            parcelOutput.print(extendedResult);

            return extendedResult;
        } catch (Exception e) {
            log.error("Ошибка при обработке данных из {}: {}", parcelSource.getDescription(), e.getMessage(), e);
        }

        return null;
    }
}
