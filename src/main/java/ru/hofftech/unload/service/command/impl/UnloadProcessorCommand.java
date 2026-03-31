package ru.hofftech.unload.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.PlacedParcel;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.service.command.ProcessorCommand;
import ru.hofftech.unload.model.core.UnloadResult;
import ru.hofftech.unload.service.output.UnloadPrepareOutputResult;

import java.util.List;

/**
 * Процессорная команда для разгрузки посылок.
 * Выполняет разгрузку машины.
 *
 */
@Slf4j
@NullMarked
@RequiredArgsConstructor
// Рекорд не может быть создан с интерфейсом
@SuppressWarnings("ClassCanBeRecord")
public class UnloadProcessorCommand implements ProcessorCommand<List<Machine>> {
    private final UnloadPrepareOutputResult unloadPrepareOutputResult;

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult execute(List<Machine> machines) {

        // Запуск разгрузки машин
        log.debug("Начало разгрузки {} машин по посылкам", machines.size());

        // Вытащим все посылки из всех машин
        List<Parcel> parcels = machines.stream()
                .flatMap(machine -> machine.parcels().stream())
                .map(PlacedParcel::parcel)
                .toList();
        // Собираем результат
        UnloadResult result =
                UnloadResult.builder().inputMachines(machines).parcels(parcels).build();
        // Преобразовываем в необходимый формат вывода
        return unloadPrepareOutputResult.output(result);
    }
}
