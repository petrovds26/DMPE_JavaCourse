package ru.hofftech.importparcel.service.parser.machine.source;

import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Machine;

import java.util.List;

/**
 * Интерфейс для источника данных машин
 */
public interface ImportParcelMachineSource<T> {

    /**
     * Получает список машин из различных источников
     * @return список машин
     */
    @NonNull
    List<Machine> getMachines(@NonNull T source);

    /**
     * @return описание источника (для логирования)
     */
    @NonNull
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}
