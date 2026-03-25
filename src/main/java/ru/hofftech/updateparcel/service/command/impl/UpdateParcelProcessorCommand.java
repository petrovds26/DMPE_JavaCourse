package ru.hofftech.updateparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.ProcessorCommand;

/**
 * Процессорная команда для обновления посылки.
 * Выполняет бизнес-логику обновления посылки в репозитории.
 */
@Slf4j
@NullMarked
@RequiredArgsConstructor
// Рекорд не может быть создан с интерфейсом
@SuppressWarnings("ClassCanBeRecord")
public class UpdateParcelProcessorCommand implements ProcessorCommand<Parcel> {
    private final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult execute(Parcel parcel) {

        if (parcelRepository.find(parcel.name()).isPresent()) {
            parcelRepository.update(parcel);

            return ProcessorCommandResult.createSuccess("Обновлена посылка. Название:" + parcel.name());
        }
        return ProcessorCommandResult.createFailure("Посылка с таким названием не существует: " + parcel.name());
    }
}
