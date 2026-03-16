package ru.hofftech.load.service.loader.strategy;

import org.jspecify.annotations.NonNull;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.load.model.enums.LoadStrategyType;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.List;

/**
 * Интерфейс для алгоритмов упаковки посылок в машины
 */
public interface LoadStrategy {

    /**
     * Упаковывает посылки в машины
     * @param parcels список посылок для упаковки
     * @return результат упаковки с разделением на успешные и проблемные
     */
    @NonNull
    LoadResult loadParcels(@NonNull List<Parcel> parcels, @NonNull List<Machine> machines);

    /**
     * @return название алгоритма
     */
    @NonNull
    LoadStrategyType getAlgorithmType();

    /**
     * @return название алгоритма
     */
    @NonNull
    default String getAlgorithmName() {
        return this.getAlgorithmType().getDescription();
    }
}
