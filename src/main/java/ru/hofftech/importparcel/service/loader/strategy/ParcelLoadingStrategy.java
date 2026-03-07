package ru.hofftech.importparcel.service.loader.strategy;

import org.jspecify.annotations.NonNull;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

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
    @NonNull
    ImportParcelResult loadParcels(@NonNull List<Parcel> parcels, @NonNull List<Machine> machines);

    /**
     * @return название алгоритма
     */
    @NonNull
    ParcelLoadingStrategyType getAlgorithmType();

    /**
     * @return название алгоритма
     */
    @NonNull
    default String getAlgorithmName() {
        return this.getAlgorithmType().getDescription();
    }
}
