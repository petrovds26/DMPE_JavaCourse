package ru.hofftech.deleteparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.ProcessorCommand;

/**
 * Процессорная команда для удаления посылки.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class DeleteParcelProcessorCommand implements ProcessorCommand {
    @NonNull
    private final ParcelRepository parcelRepository;

    @NonNull
    private final String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull ProcessorCommandResult execute() {

        if (name.isEmpty()) {
            return ProcessorCommandResult.builder()
                    .success(false)
                    .message("Не указано название посылки")
                    .build();
        }

        if (parcelRepository.find(name).isEmpty()) {
            return ProcessorCommandResult.builder()
                    .success(false)
                    .message(String.format("Посылка с названием %s не существует", name))
                    .build();
        }

        parcelRepository.delete(name);

        return ProcessorCommandResult.builder()
                .success(false)
                .message(String.format("Удаление посылки %s завершено", name))
                .build();
    }
}
