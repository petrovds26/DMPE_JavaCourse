package ru.hofftech.unload.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.core.ParserMachineProcessorResult;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.model.enums.FileType;
import ru.hofftech.shared.service.FileSaveService;
import ru.hofftech.shared.service.command.console.ConsoleCommand;
import ru.hofftech.shared.service.parser.ParserMachineProcessor;
import ru.hofftech.shared.service.parser.ParserParams;
import ru.hofftech.shared.util.FileTypeUtil;
import ru.hofftech.shared.validation.impl.InputFilePathValidator;
import ru.hofftech.shared.validation.impl.OutputFilePathValidator;
import ru.hofftech.unload.model.enums.UnloadInputMachineType;
import ru.hofftech.unload.model.enums.UnloadOutputType;
import ru.hofftech.unload.model.params.UnloadConsoleCommandParams;
import ru.hofftech.unload.service.output.UnloadOutputPrepareService;
import ru.hofftech.unload.service.output.UnloadPrepareOutputResult;
import ru.hofftech.unload.service.parser.source.UnloadParserMachineService;

import java.util.List;

/**
 * Консольная команда для загрузки машин посылками.
 * Формат команды: load --parcelsFile <файл>|--parcelsText <текст> --output <тип> --strategy <ID> --trucks <описание>
 *
 */
@Slf4j
@NullMarked
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class UnloadConsoleCommand implements ConsoleCommand {
    private final ParserParams parserParams;

    private final InputFilePathValidator inputFilePathValidator;

    private final OutputFilePathValidator outputFilePathValidator;

    private final UnloadParserMachineService parserMachineService;

    private final UnloadOutputPrepareService outputPrepareService;

    private final FileSaveService fileSaveService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return ConsoleCommandType.UNLOAD.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Разгрузка машин. Используйте --help для справки.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String input) {
        return input.trim().startsWith(ConsoleCommandType.UNLOAD.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(String input) {

        UnloadConsoleCommandParams params = new UnloadConsoleCommandParams();
        if (!parserParams.parserCommandLine(params, input)) {
            return;
        }

        log.debug("Загрузка машин посылками: {}", input);

        // Подбор парсинга машин исходя из источника
        UnloadInputMachineType unloadInputMachineType = defineInputParcelType(params);
        if (unloadInputMachineType == null) {
            log.info("Доступные типы файла с машинами: {}", parserMachineService.getAvailableTypesDescription());
            return;
        }

        // Проверка наличия парсера через сервис
        ParserMachineProcessor<String> parserMachineProcessor = parserMachineService.getParser(unloadInputMachineType);
        if (parserMachineProcessor == null) {
            log.error(
                    "Ошибки валидации входящих параметров: Тип ввода {} не поддерживается для распознавания",
                    unloadInputMachineType);
            log.info("Доступные типы файла с машинами: {}", parserMachineService.getAvailableTypesDescription());
            return;
        }

        // Подбор исходящего формата вывода исходя их параметров
        UnloadOutputType unloadOutputType = defineOutputType(params);
        if (unloadOutputType == null) {
            log.info("Доступные типы исходящего файла: {}", outputPrepareService.getAvailableTypesDescription());
            return;
        }

        // Проверка наличия подготовителя вывода через сервис
        UnloadPrepareOutputResult unloadPrepareOutputResult = outputPrepareService.getPreparer(unloadOutputType);

        if (unloadPrepareOutputResult == null) {
            log.error(
                    "Ошибки валидации исходящих параметров: Тип вывода {} не поддерживается для распознавания",
                    unloadOutputType);
            log.info("Доступные типы исходящего файла: {}", outputPrepareService.getAvailableTypesDescription());
            return;
        }

        // Распознавание машин через сервис
        ParserMachineProcessorResult parserMachineProcessorResult =
                parserMachineProcessor.transform(params.getInputFile());

        if (parserMachineProcessorResult.machines().isEmpty()) {
            log.error("Ошибки при распознавании посылок: {}", parserMachineProcessorResult.getErrorsAsString());
            return;
        }

        UnloadProcessorCommand processorCommand = new UnloadProcessorCommand(unloadPrepareOutputResult);

        ProcessorCommandResult commandResult = processorCommand.execute(parserMachineProcessorResult.machines());

        if (unloadOutputType.needSaveFile()) {
            commandResult = fileSaveService.saveFile(commandResult.message(), params.getOutputFile());
        }

        if (commandResult.success()) {
            log.info(commandResult.message());
        } else {
            log.warn(commandResult.message());
        }
    }

    /**
     * Определяет тип выходного формата на основе параметров команды.
     *
     * @param params параметры команды (не может быть null)
     * @return тип выходного формата или null, если не удалось определить
     */
    @Nullable
    private UnloadOutputType defineOutputType(UnloadConsoleCommandParams params) {
        UnloadOutputType loadOutputType = UnloadOutputType.fromString(params.getOutputType());
        if (loadOutputType == null) {
            log.error("Ошибки валидации параметра Выходной файл. Тип формата не найден");
            return null;
        }

        FileType fileType = loadOutputType.loadOutputType2FileType();

        if (fileType == null) {
            if (!params.getOutputFile().isBlank()) {
                log.error(
                        "Ошибки валидации параметра Выходной файл: Указан выходной файл, но сохранение не предусматривается");
                return null;
            }
        } else {
            if (params.getOutputFile().isBlank()) {
                log.error("Ошибки валидации параметра Выходной файл: Файл не указан");
                return null;
            }

            List<String> outputErrors = outputFilePathValidator.validate(params.getOutputFile());
            if (!outputErrors.isEmpty()) {
                log.error("Ошибки валидации параметра Выходной файл: {}", String.join("; ", outputErrors));
                return null;
            }
        }

        return loadOutputType;
    }

    /**
     * Определяет тип входных данных на основе параметров команды.
     *
     * @param params параметры команды (не может быть null)
     * @return тип входных данных или null, если не удалось определить
     */
    @Nullable
    private UnloadInputMachineType defineInputParcelType(UnloadConsoleCommandParams params) {
        List<String> inputErrors = inputFilePathValidator.validate(params.getInputFile());
        if (!inputErrors.isEmpty()) {
            log.error("Ошибки валидации параметра Входной файл: {}", String.join("; ", inputErrors));
            return null;
        }

        FileType inputFileType = FileTypeUtil.fromFilename(params.getInputFile());
        if (inputFileType == null) {
            log.error("Ошибки валидации параметра Входной файл: Тип файла не определен");
            return null;
        }
        UnloadInputMachineType unloadInputMachineType =
                UnloadInputMachineType.fileType2LoadInputParcelType(inputFileType);
        if (unloadInputMachineType == null) {
            log.error("Ошибки валидации параметра Входной файл: Тип файла {} не поддерживается", inputFileType);
            return null;
        }

        return unloadInputMachineType;
    }
}
