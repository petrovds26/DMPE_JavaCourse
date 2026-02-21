package ru.hofftech.service.loader.strategy;

import ru.hofftech.model.dto.LoadingResult;
import ru.hofftech.model.entity.Parcel;

import java.util.List;

/**
 * Интерфейс для алгоритмов упаковки посылок в машины
 */
public interface ParcelLoadingStrategy {

    /**
     * Упаковывает посылки в машины
     * @param parcels список посылок для упаковки
     * @return результат упаковки с разделением на успешные и проблемные
     */
    LoadingResult loadParcels(List<Parcel> parcels);

    /**
     * @return название алгоритма
     */
    ParcelLoadingStrategyType getAlgorithmType();

    /**
     * @return название алгоритма
     */
    default String getAlgorithmName() {
        return this.getAlgorithmType().getDescription();
    }
}
