package ru.hofftech.deleteparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.ProcessorCommand;

/**
 * Процессорная команда для удаления посылки.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public class DeleteParcelProcessorCommand implements ProcessorCommand<String> {
    private final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult execute(String name) {

        if (name.isEmpty()) {
            return ProcessorCommandResult.createFailure("Не указано название посылки");
        }

        if (parcelRepository.find(name).isEmpty()) {
            return ProcessorCommandResult.createFailure(String.format("Посылка с названием %s не существует", name));
        }

        parcelRepository.delete(name);

        return ProcessorCommandResult.createSuccess(String.format("Удаление посылки %s завершено", name));
    }
}
