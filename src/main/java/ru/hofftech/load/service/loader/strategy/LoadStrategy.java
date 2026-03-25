package ru.hofftech.load.service.loader.strategy;

import org.jspecify.annotations.NullMarked;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.load.model.enums.LoadStrategyType;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

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
