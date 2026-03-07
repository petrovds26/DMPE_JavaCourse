package ru.hofftech.importparcel.service.loader.strategy;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importparcel.service.loader.strategy.impl.BalancedPackingStrategy;
import ru.hofftech.importparcel.service.loader.strategy.impl.DensePackingStrategy;
import ru.hofftech.importparcel.service.loader.strategy.impl.OneParcelPerMachineStrategy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы со стратегиями упаковки
 */
@Slf4j
public class ParcelLoadingStrategyService {
    @NonNull
    private final List<ParcelLoadingStrategy> strategies;

    public ParcelLoadingStrategyService() {
        this.strategies =
                List.of(new OneParcelPerMachineStrategy(), new DensePackingStrategy(), new BalancedPackingStrategy());
    }

    /**
     * Получить стратегию по ID
     * @param id идентификатор стратегии
     * @return стратегия или null, если не найдена
     */
    @Nullable
    public ParcelLoadingStrategy getStrategyById(int id) {
        return strategies.stream()
                .filter(strategy -> strategy.getAlgorithmType().getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Формирует описание доступных стратегий
     */
    @NonNull
    public String getAvailableStrategiesDescription() {
        return strategies.stream()
                .map(strategy ->
                        String.format("%d - %s", strategy.getAlgorithmType().getId(), strategy.getAlgorithmName()))
                .collect(Collectors.joining("; "));
    }
}
