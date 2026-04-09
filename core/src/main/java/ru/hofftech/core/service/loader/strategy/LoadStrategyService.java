package ru.hofftech.core.service.loader.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы со стратегиями упаковки.
 * <p>
 * Предоставляет методы для получения стратегий по ID и описания доступных стратегий.
 */
@Slf4j
@RequiredArgsConstructor
@NullMarked
@Service
public class LoadStrategyService {
    private final List<LoadStrategy> strategies;

    /**
     * Получает стратегию по ID.
     *
     * @param id идентификатор стратегии
     * @return стратегия или null, если не найдена
     */
    @Nullable
    public LoadStrategy getStrategyById(int id) {
        return strategies.stream()
                .filter(strategy -> strategy.getAlgorithmType().getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Формирует описание доступных стратегий.
     *
     * @return строка с описанием формата "ID - название"
     */
    public String getAvailableStrategiesDescription() {
        return strategies.stream()
                .map(strategy ->
                        String.format("%d - %s", strategy.getAlgorithmType().getId(), strategy.getAlgorithmName()))
                .collect(Collectors.joining("; "));
    }
}
