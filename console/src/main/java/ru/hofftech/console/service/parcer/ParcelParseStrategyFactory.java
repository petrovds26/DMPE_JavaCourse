package ru.hofftech.console.service.parcer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.hofftech.console.exception.ValidateException;
import ru.hofftech.console.model.enums.LoadInputParcelType;
import ru.hofftech.console.service.parcer.parcelnamestrategy.ParcelNameParseStrategy;

import java.util.List;

/**
 * Фабрика стратегий парсинга названий посылок.
 * <p>
 * Автоматически собирает все стратегии, реализующие интерфейс ParcelNameParseStrategy,
 * и предоставляет методы для получения стратегии по типу источника.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@NullMarked
public class ParcelParseStrategyFactory {

    private final List<ParcelNameParseStrategy> strategies;

    /**
     * Получает стратегию парсинга по типу источника.
     *
     * @param type тип источника
     * @return стратегия парсинга или null, если не найдена
     */
    @Nullable
    public ParcelNameParseStrategy getStrategy(LoadInputParcelType type) {
        return strategies.stream()
                .filter(strategy -> strategy.getSupportedType() == type)
                .findFirst()
                .orElse(null);
    }

    /**
     * Получает стратегию парсинга по типу источника.
     *
     * @param type тип источника
     * @return стратегия парсинга
     * @throws ValidateException если стратегия не найдена
     */
    public ParcelNameParseStrategy getStrategyOrThrow(LoadInputParcelType type) {
        return strategies.stream()
                .filter(strategy -> strategy.getSupportedType() == type)
                .findFirst()
                .orElseThrow(() -> new ValidateException("Не поддерживаемый тип ввода: " + type));
    }
}
