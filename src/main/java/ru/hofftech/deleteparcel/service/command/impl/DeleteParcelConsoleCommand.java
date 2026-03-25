package ru.hofftech.deleteparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.deleteparcel.model.params.DeleteParcelConsoleCommandParams;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.console.ConsoleCommand;
import ru.hofftech.shared.service.parser.ParserParams;

/**
 * Консольная команда для удаления посылки.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public class DeleteParcelConsoleCommand implements ConsoleCommand {
    private final ParserParams parserParams;

    private final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return ConsoleCommandType.DELETE_PARCEL.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Удаление посылок. Используйте --help для справки.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String input) {
        return input.trim().startsWith(ConsoleCommandType.DELETE_PARCEL + " ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(String input) {

        DeleteParcelConsoleCommandParams params = new DeleteParcelConsoleCommandParams();
        if (!parserParams.parserCommandLine(params, input)) {
            return;
        }

        String name = params.getName();

        log.debug("Удаление посылки: {}", name);

        DeleteParcelProcessorCommand processorCommand = new DeleteParcelProcessorCommand(parcelRepository);
        ProcessorCommandResult processorCommandResult = processorCommand.execute(name);

        if (processorCommandResult.success()) {
            log.info(processorCommandResult.message());
        } else {
            log.warn(processorCommandResult.message());
        }
    }
}
