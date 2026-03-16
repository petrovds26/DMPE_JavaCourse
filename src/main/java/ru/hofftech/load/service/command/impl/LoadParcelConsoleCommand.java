package ru.hofftech.load.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.load.model.enums.LoadInputParcelType;
import ru.hofftech.load.model.enums.LoadOutputType;
import ru.hofftech.load.model.params.LoadConsoleCommandParams;
import ru.hofftech.load.model.params.LoadProcessorCommandParams;
import ru.hofftech.load.service.loader.strategy.LoadStrategy;
import ru.hofftech.load.service.loader.strategy.LoadStrategyService;
import ru.hofftech.load.service.output.LoadOutputPrepareService;
import ru.hofftech.load.service.output.LoadPrepareOutputResult;
import ru.hofftech.load.service.parser.LoadParcelParserService;
import ru.hofftech.shared.model.core.ParserMachineProcessorResult;
import ru.hofftech.shared.model.core.ParserParcelProcessorResult;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.model.enums.FileType;
import ru.hofftech.shared.service.FileSaveService;
import ru.hofftech.shared.service.command.console.ConsoleCommand;
import ru.hofftech.shared.service.parser.ParserParams;
import ru.hofftech.shared.service.parser.ParserParcelProcessor;
import ru.hofftech.shared.service.parser.impl.ParserMachineFromFormString;
import ru.hofftech.shared.util.FileTypeUtil;
import ru.hofftech.shared.validation.impl.InputFilePathValidator;
import ru.hofftech.shared.validation.impl.OutputFilePathValidator;

import java.util.List;
import java.util.stream.Stream;

/**
 * Консольная команда для загрузки машин посылками.
 * Формат команды: load --parcelsFile <файл>|--parcelsText <текст> --output <тип> --strategy <ID> --trucks <описание>
 *
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class LoadParcelConsoleCommand implements ConsoleCommand {
    @NonNull
    private final ParserParams parserParams;

    @NonNull
    private final InputFilePathValidator inputFilePathValidator;

    @NonNull
    private final OutputFilePathValidator outputFilePathValidator;

    @NonNull
    private final LoadStrategyService strategyService;

    @NonNull
    private final LoadParcelParserService parserService;

    @NonNull
    private final ParserMachineFromFormString parserMachineFromFormString;

    @NonNull
    private final LoadOutputPrepareService outputPrepareService;

    @NonNull
    private final FileSaveService fileSaveService;

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getName() {
        return ConsoleCommandType.LOAD.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getDescription() {
        return "Загрузка машин. Используйте --help для справки.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@NonNull String input) {
        return input.trim().startsWith(ConsoleCommandType.LOAD.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NonNull String input) {

        LoadConsoleCommandParams params = new LoadConsoleCommandParams();
        if (!parserParams.parserCommandLine(params, input)) {
            return;
        }

        log.debug("Загрузка машин посылками: {}", input);

        // Подбор парсинга посылок исходя из источника
        LoadInputParcelType loadInputParcelType = defineInputParcelType(params);
        if (loadInputParcelType == null) {
            return;
        }

        // Проверка наличия парсера через сервис
        ParserParcelProcessor<String> parserParcelProcessor = parserService.getParser(loadInputParcelType);
        if (parserParcelProcessor == null) {
            log.error(
                    "Ошибки валидации входящих параметров: Тип ввода {} не поддерживается для распознавания",
                    loadInputParcelType);
            log.info("Доступные типы: {}", parserService.getAvailableTypesDescription());
            return;
        }

        // Подбор исходящего формата вывода исходя их параметров
        LoadOutputType loadOutputType = defineOutputType(params);
        if (loadOutputType == null) {
            return;
        }

        // Проверка наличия подготовителя вывода через сервис
        LoadPrepareOutputResult loadPrepareOutputResult = outputPrepareService.getPreparer(loadOutputType);

        if (loadPrepareOutputResult == null) {
            log.error(
                    "Ошибки валидации исходящих параметров: Тип вывода {} не поддерживается для распознавания",
                    loadOutputType);
            log.info("Доступные типы: {}", outputPrepareService.getAvailableTypesDescription());
            return;
        }

        // Парсинг машин
        ParserMachineProcessorResult parserMachineProcessorResult =
                parserMachineFromFormString.transform(params.getTruck());

        if (parserMachineProcessorResult.machines().isEmpty()) {
            log.error("Ошибки при распознавании машин: {}", parserMachineProcessorResult.getErrorsAsString());
            return;
        }

        // Парсинг стратегии
        LoadStrategy strategy = strategyService.getStrategyById(params.getStrategyId());
        if (strategy == null) {
            log.error(
                    "Стратегия с ID {} не найдена. Доступные ID: {}",
                    params.getStrategyId(),
                    strategyService.getAvailableStrategiesDescription());
            return;
        }

        // Распознавание посылок через сервис
        ParserParcelProcessorResult parserParcelProcessorResult = parserParcelProcessor.transform(
                loadInputParcelType == LoadInputParcelType.TEXT
                        ? params.getInputParcelText()
                        : params.getInputParcelFile());

        if (parserParcelProcessorResult.parcels().isEmpty()) {
            log.error("Ошибки при распознавании посылок: {}", parserParcelProcessorResult.getErrorsAsString());
            return;
        }

        LoadProcessorCommandParams loadProcessorCommandParams = LoadProcessorCommandParams.builder()
                .parcels(parserParcelProcessorResult.parcels())
                .machines(parserMachineProcessorResult.machines())
                .prevErrors(Stream.concat(
                                parserParcelProcessorResult.errors().stream(),
                                parserMachineProcessorResult.errors().stream())
                        .toList())
                .build();

        LoadParcelProcessorCommand processorCommand = new LoadParcelProcessorCommand(strategy, loadPrepareOutputResult);

        ProcessorCommandResult commandResult = processorCommand.execute(loadProcessorCommandParams);

        if (loadOutputType.needSaveFile()) {
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
    private LoadOutputType defineOutputType(@NonNull LoadConsoleCommandParams params) {
        LoadOutputType loadOutputType = LoadOutputType.fromString(params.getOutputType());
        if (loadOutputType == null) {
            log.error("Ошибки валидации параметра Выходной файл. Тип формата не найден");
            return null;
        }

        FileType fileType = loadOutputType.loadOutputType2FileType();

        if (fileType == null) {
            if (params.getOutputFile() != null && !params.getOutputFile().isBlank()) {
                log.error(
                        "Ошибки валидации параметра Выходной файл: Указан выходной файл, но сохранение не предусматривается");
                return null;
            }
        } else {
            if (params.getOutputFile() == null || params.getOutputFile().isBlank()) {
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
    private LoadInputParcelType defineInputParcelType(@NonNull LoadConsoleCommandParams params) {
        if (params.getInputParcelFile() == null || params.getInputParcelFile().isBlank()) {
            return LoadInputParcelType.TEXT;
        }

        List<String> inputErrors = inputFilePathValidator.validate(params.getInputParcelFile());
        if (!inputErrors.isEmpty()) {
            log.error("Ошибки валидации параметра Входной файл: {}", String.join("; ", inputErrors));
            return null;
        }

        FileType inputFileType = FileTypeUtil.fromFilename(params.getInputParcelFile());
        if (inputFileType == null) {
            log.error("Ошибки валидации параметра Входной файл: Тип файла не определен");
            return null;
        }
        LoadInputParcelType loadInputParcelType = LoadInputParcelType.fileType2LoadInputParcelType(inputFileType);
        if (loadInputParcelType == null) {
            log.error("Ошибки валидации параметра Входной файл: Тип файла {} не поддерживается", inputFileType);
            return null;
        }

        return loadInputParcelType;
    }
}
