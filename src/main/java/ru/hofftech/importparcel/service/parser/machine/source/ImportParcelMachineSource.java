package ru.hofftech.importparcel.service.parser.machine.source;

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
    List<Machine> getMachines(T source);

    /**
     * @return описание источника (для логирования)
     */
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}
