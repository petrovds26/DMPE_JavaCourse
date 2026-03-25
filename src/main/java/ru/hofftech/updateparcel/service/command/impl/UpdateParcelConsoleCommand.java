package ru.hofftech.updateparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.ParserParcelProcessorResult;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.dto.ParcelFormDto;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.console.ConsoleCommand;
import ru.hofftech.shared.service.parser.ParserParams;
import ru.hofftech.shared.service.parser.impl.ParserParcelFromFormDto;
import ru.hofftech.updateparcel.model.params.UpdateParcelConsoleCommandParams;

import java.util.List;

/**
 * Консольная команда для обновления посылки.
 * Формат команды: update_parcel --name <название> --form <форма> --symbol <символ>
 */
@Slf4j
@NullMarked
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class UpdateParcelConsoleCommand implements ConsoleCommand {
    private final ParserParams parserParams;

    private final ParserParcelFromFormDto parserParcelProcessor;

    private final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return ConsoleCommandType.UPDATE_PARCEL.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Обновление посылок. Используйте --help для справки.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String input) {
        return input.trim().startsWith(ConsoleCommandType.UPDATE_PARCEL + " ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(String input) {
        UpdateParcelConsoleCommandParams params = new UpdateParcelConsoleCommandParams();
        if (!parserParams.parserCommandLine(params, input)) {
            return;
        }

        ParcelFormDto parcelFormDto = ParcelFormDto.builder()
                .form(params.getForm())
                .name(params.getName())
                .symbol(params.getSymbol())
                .build();

        log.debug("Обновление посылки: {} {} {}", parcelFormDto.name(), parcelFormDto.form(), parcelFormDto.symbol());

        ParserParcelProcessorResult processorResult = parserParcelProcessor.transform(List.of(parcelFormDto));

        if (processorResult.parcels().isEmpty()) {
            log.warn(
                    processorResult.getErrorsAsString().isBlank()
                            ? "Не удалось распознать посылку"
                            : processorResult.getErrorsAsString());
            return;
        }

        UpdateParcelProcessorCommand processorCommand = new UpdateParcelProcessorCommand(parcelRepository);
        ProcessorCommandResult processorCommandResult =
                processorCommand.execute(processorResult.parcels().getFirst());

        if (processorCommandResult.success()) {
            log.info(processorCommandResult.message());
        } else {
            log.warn(processorCommandResult.message());
        }
    }
}
