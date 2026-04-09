package ru.hofftech.core.service.loader.strategy;

import org.jspecify.annotations.NullMarked;
import ru.hofftech.core.model.core.LoadResult;
import ru.hofftech.core.model.core.Machine;
import ru.hofftech.core.model.core.Parcel;
import ru.hofftech.shared.model.enums.LoadStrategyType;

import java.util.List;

/**
 * Интерфейс для алгоритмов упаковки посылок в машины
 */
@NullMarked
public interface LoadStrategy {

    /**
     * Упаковывает посылки в машины
     * @param parcels список посылок для упаковки
     * @return результат упаковки с разделением на успешные и проблемные
     */
    LoadResult loadParcels(List<Parcel> parcels, List<Machine> machines);

    /**
     * @return название алгоритма
     */
    LoadStrategyType getAlgorithmType();

    /**
     * @return название алгоритма
     */
    default String getAlgorithmName() {
        return this.getAlgorithmType().getDescription();
    }
}
