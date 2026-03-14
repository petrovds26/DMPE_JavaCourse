package ru.hofftech.readparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.readparcel.model.params.ReadParcelConsoleCommandParams;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.console.ConsoleCommand;
import ru.hofftech.shared.service.parser.ParserParams;

/**
 * Консольная команда для чтения посылок.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class ReadParcelConsoleCommand implements ConsoleCommand {
    @NonNull
    private final ParserParams parserParams;

    @NonNull
    private final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getName() {
        return ConsoleCommandType.READ_PARCEL.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getDescription() {
        return "Чтение посылок. Используйте --help для справки.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@NonNull String input) {
        return input.trim().startsWith(ConsoleCommandType.READ_PARCEL.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NonNull String input) {

        ReadParcelConsoleCommandParams params = new ReadParcelConsoleCommandParams();
        if (!parserParams.parserCommandLine(params, input)) {
            return;
        }

        log.debug("Чтение посылок: {}", params.getName());

        ReadParcelProcessorCommand processorCommand =
                new ReadParcelProcessorCommand(parcelRepository, params.getName());
        ProcessorCommandResult processorCommandResult = processorCommand.execute();

        if (processorCommandResult.success()) {
            log.info(processorCommandResult.message());
        } else {
            log.warn(processorCommandResult.message());
        }
    }
}
