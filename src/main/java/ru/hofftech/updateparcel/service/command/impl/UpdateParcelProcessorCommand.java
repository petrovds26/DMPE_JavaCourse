package ru.hofftech.updateparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.ProcessorCommand;

/**
 * Процессорная команда для обновления посылки.
 */
@Slf4j
@RequiredArgsConstructor
// Рекорд не может быть создан с интерфейсом
@SuppressWarnings("ClassCanBeRecord")
public class UpdateParcelProcessorCommand implements ProcessorCommand {
    @NonNull
    private final ParcelRepository parcelRepository;

    @NonNull
    private final Parcel parcel;

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull ProcessorCommandResult execute() {

        if (parcelRepository.find(parcel.name()).isPresent()) {
            parcelRepository.update(parcel);

            return ProcessorCommandResult.builder()
                    .success(true)
                    .message("Обновлена посылка. Название:" + parcel.name())
                    .build();
        }
        return ProcessorCommandResult.builder()
                .success(false)
                .message("Посылка с таким названием не существует: " + parcel.name())
                .build();
    }
}
