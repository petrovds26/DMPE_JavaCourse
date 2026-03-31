package ru.hofftech.createparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.ProcessorCommand;

/**
 * Процессорная команда для создания посылки.
 * Выполняет бизнес-логику создания посылки в репозитории.
 */
@Slf4j
@RequiredArgsConstructor
// Рекорд не может быть создан с интерфейсом
@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public class CreateParcelProcessorCommand implements ProcessorCommand<Parcel> {
    private final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult execute(Parcel parcel) {

        if (parcelRepository.find(parcel.name()).isEmpty()) {
            parcelRepository.insert(parcel);

            return ProcessorCommandResult.createSuccess("Создана посылка. Название:" + parcel.name());
        }
        return ProcessorCommandResult.createFailure("Посылка с таким названием уже существует: {}" + parcel.name());
    }
}
