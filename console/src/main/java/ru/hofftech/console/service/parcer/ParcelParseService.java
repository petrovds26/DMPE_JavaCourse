package ru.hofftech.console.service.parcer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.hofftech.console.model.enums.LoadInputParcelType;
import ru.hofftech.console.service.parcer.parcelnamestrategy.ParcelNameParseStrategy;
import ru.hofftech.shared.model.dto.ParcelNameRequestDto;

import java.util.List;

/**
 * Сервис для парсинга посылок из различных источников.
 * <p>
 * Определяет тип источника (текст, TXT файл, JSON файл) и
 * делегирует парсинг соответствующей стратегии.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@NullMarked
public class ParcelParseService {

    private final ParcelSourceTypeResolver typeResolver;
    private final ParcelParseStrategyFactory strategyFactory;

    /**
     * Парсит источник и возвращает список названий посылок
     *
     * @param inputParcelFile путь к файлу (опционально)
     * @param inputParcelText текст с посылками (опционально)
     * @return список названий посылок
     */
    public List<ParcelNameRequestDto> parseParcels(String inputParcelFile, String inputParcelText) {
        // 1. Определяем тип источника
        LoadInputParcelType sourceType = typeResolver.resolve(inputParcelFile, inputParcelText);

        // 2. Получаем соответствующую стратегию
        ParcelNameParseStrategy strategy = strategyFactory.getStrategyOrThrow(sourceType);

        // 3. Получаем источник данных (файл или текст)
        String source = sourceType == LoadInputParcelType.TEXT ? inputParcelText : inputParcelFile;

        // 4. Парсим
        List<ParcelNameRequestDto> parcelNames = strategy.parse(source);

        if (parcelNames.isEmpty()) {
            log.warn("Не найдено ни одной посылки в источнике");
        } else {
            log.info("Успешно распознано {} посылок", parcelNames.size());
        }

        return parcelNames;
    }
}
