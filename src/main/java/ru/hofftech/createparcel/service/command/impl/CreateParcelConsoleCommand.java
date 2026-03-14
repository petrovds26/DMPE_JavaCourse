package ru.hofftech.createparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.createparcel.model.params.CreateParcelConsoleCommandParams;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.core.TransformParcelResult;
import ru.hofftech.shared.model.dto.ParcelFormDto;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.console.ConsoleCommand;
import ru.hofftech.shared.service.parser.ParserParams;
import ru.hofftech.shared.service.parser.ParserParcelProcessor;

/**
 * Консольная команда для создания новой посылки.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class CreateParcelConsoleCommand implements ConsoleCommand {
    @NonNull
    private final ParserParams parserParams;

    @NonNull
    private final ParserParcelProcessor parserParcelProcessor;

    @NonNull
    private final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getName() {
        return ConsoleCommandType.CREATE_PARCEL.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getDescription() {
        return "Создание посылок. Используйте --help для справки.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@NonNull String input) {
        return input.trim().startsWith(ConsoleCommandType.CREATE_PARCEL + " ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NonNull String input) {

        CreateParcelConsoleCommandParams params = new CreateParcelConsoleCommandParams();
        if (!parserParams.parserCommandLine(params, input)) {
            return;
        }

        if (params.getForm() == null || params.getName() == null || params.getSymbol() == null) {
            log.warn("Не указаны обязательные параметры.");
            return;
        }

        ParcelFormDto parcelFormDto = ParcelFormDto.builder()
                .form(params.getForm())
                .name(params.getName())
                .symbol(params.getSymbol())
                .build();

        if (parcelFormDto == null) {
            return;
        }
        log.debug("Создание посылки: {} {} {}", parcelFormDto.name(), parcelFormDto.form(), parcelFormDto.symbol());

        TransformParcelResult transformParcelResult = parserParcelProcessor.transform(parcelFormDto);

        if (transformParcelResult.parcel() == null) {
            log.warn(
                    transformParcelResult.error() != null
                            ? transformParcelResult.error()
                            : "Не удалось распознать посылку");
            return;
        }

        CreateParcelProcessorCommand processorCommand =
                new CreateParcelProcessorCommand(parcelRepository, transformParcelResult.parcel());
        ProcessorCommandResult processorCommandResult = processorCommand.execute();

        if (processorCommandResult.success()) {
            log.info(processorCommandResult.message());
        } else {
            log.warn(processorCommandResult.message());
        }
    }
}
