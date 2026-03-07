package ru.hofftech.importparcel.service.command.impl;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importparcel.model.params.ImportParcelConsoleCommandParams;
import ru.hofftech.importparcel.model.params.ImportParcelParams;
import ru.hofftech.importparcel.service.ImportParcelProcessor;
import ru.hofftech.importparcel.service.loader.strategy.ParcelLoadingStrategy;
import ru.hofftech.importparcel.service.loader.strategy.ParcelLoadingStrategyService;
import ru.hofftech.importparcel.service.output.ImportParcelOutput;
import ru.hofftech.importparcel.service.output.ImportParcelOutputService;
import ru.hofftech.importparcel.service.parser.machine.source.ImportParcelMachineSource;
import ru.hofftech.importparcel.service.parser.machine.source.impl.ImportParcelMachineIntegerDefaultSource;
import ru.hofftech.importparcel.service.parser.parcel.source.ImportParcelFileSource;
import ru.hofftech.importparcel.service.parser.parcel.source.ImportParcelFileSourceService;
import ru.hofftech.importparcel.validation.impl.ParcelGridValidator;
import ru.hofftech.importparcel.validation.impl.ParcelListStringValidator;
import ru.hofftech.shared.model.enums.FileType;
import ru.hofftech.shared.service.command.ConsoleCommand;
import ru.hofftech.shared.service.command.ConsoleCommandType;
import ru.hofftech.shared.service.parser.ParcelBuilder;
import ru.hofftech.shared.service.parser.ParcelNormalizer;
import ru.hofftech.shared.util.FileTypeUtil;
import ru.hofftech.shared.validation.impl.InputFilePathValidator;
import ru.hofftech.shared.validation.impl.OutputFilePathValidator;

import java.util.List;

@Slf4j
@AllArgsConstructor

/*
 Консольная команда для импорта и упаковки посылок.
 Поддерживает форматы .txt и .json, различные стратегии упаковки
 и возможность указания количества машин.
*/
@SuppressWarnings("ClassCanBeRecord")
public class ImportParcelConsoleCommand implements ConsoleCommand {
    @NonNull
    private final ParcelLoadingStrategyService strategyService;

    @NonNull
    private final ImportParcelFileSourceService importParcelFileSourceService;

    @NonNull
    private final ImportParcelMachineSource<Integer> importParcelMachineSource;

    @NonNull
    private final ImportParcelOutputService importParcelOutputService;

    @NonNull
    private final InputFilePathValidator inputFilePathValidator;

    @NonNull
    private final OutputFilePathValidator outputFilePathValidator;

    @NonNull
    private final ParcelNormalizer normalizer;

    @NonNull
    private final ParcelBuilder parcelBuilder;

    @NonNull
    private final ParcelListStringValidator stringValidator;

    @NonNull
    private final ParcelGridValidator gridValidator;

    /**
     * Конструктор по умолчанию, инициализирующий все зависимости.
     */
    public ImportParcelConsoleCommand() {
        this.strategyService = new ParcelLoadingStrategyService();
        this.importParcelFileSourceService = new ImportParcelFileSourceService();
        this.importParcelMachineSource = new ImportParcelMachineIntegerDefaultSource();
        this.importParcelOutputService = new ImportParcelOutputService();
        this.normalizer = new ParcelNormalizer();
        this.parcelBuilder = new ParcelBuilder();
        this.stringValidator = new ParcelListStringValidator();
        this.inputFilePathValidator = new InputFilePathValidator();
        this.outputFilePathValidator = new OutputFilePathValidator();
        this.gridValidator = new ParcelGridValidator();
    }

    @Override
    @NonNull
    public String getName() {
        return ConsoleCommandType.IMPORT_PARCEL.toString();
    }

    @Override
    @NonNull
    public String getDescription() {
        return "Импорт посылок из файла. Поддерживаются форматы .txt и .json. " + "Используйте --help для справки.";
    }

    @Override
    public boolean matches(@NonNull String input) {
        return input.trim().startsWith(ConsoleCommandType.IMPORT_PARCEL + " ");
    }

    @Override
    public void execute(@NonNull String input) {

        ImportParcelParams importParcelParams = parseParams(input);

        if (importParcelParams == null) {
            return;
        }

        // Валидация входных параметров
        List<String> inputErrors = inputFilePathValidator.validate(importParcelParams.inputFilePath());
        if (!inputErrors.isEmpty()) {
            log.error("Ошибки валидации параметра Входной файл: {}", String.join("; ", inputErrors));
            return;
        }

        FileType inputFileType = FileTypeUtil.fromFilename(importParcelParams.inputFilePath());
        if (inputFileType == null) {
            log.error("Ошибки валидации параметра Входной файл: Тип файла не определен");
            return;
        }

        ImportParcelFileSource<String> fileParcelSource =
                importParcelFileSourceService.getSourceByFileType(inputFileType);

        if (fileParcelSource == null) {
            log.error(
                    "Не удалось определить тип входящего файла. Поддерживаются форматы {}. Проверьте входной файл: {}",
                    importParcelFileSourceService.getAvailableFileExtensionDescription(),
                    importParcelParams.inputFilePath());
            return;
        }

        if (importParcelParams.outputFilePath() != null) {
            List<String> outputErrors = outputFilePathValidator.validate(importParcelParams.outputFilePath());
            if (!outputErrors.isEmpty()) {
                log.error("Ошибки валидации параметра Выходной файл: {}", String.join("; ", outputErrors));
                return;
            }
        }

        FileType fileTypeOutput = FileTypeUtil.fromFilename(importParcelParams.outputFilePath());

        if (fileTypeOutput == null
                && importParcelParams.outputFilePath() != null
                && !importParcelParams.outputFilePath().isEmpty()) {
            log.error(
                    "Не удалось определить тип исходящего файла. Поддерживаются форматы {}. Проверьте исходящий файл: {}",
                    importParcelOutputService.getAvailableFileExtensionDescription(),
                    importParcelParams.outputFilePath());
            return;
        }

        ImportParcelOutput output = importParcelOutputService.getOutputByFileType(fileTypeOutput);

        if (output == null) {
            log.error(
                    "Не удалось определить тип исходящего файла. Поддерживаются форматы {}. Проверьте исходящий файл: {}",
                    importParcelOutputService.getAvailableFileExtensionDescription(),
                    importParcelParams.outputFilePath());
            return;
        }

        ParcelLoadingStrategy strategy = strategyService.getStrategyById(importParcelParams.strategyId());
        if (strategy == null) {
            log.error(
                    "Стратегия с ID {} не найдена. Доступные ID: {}",
                    importParcelParams.strategyId(),
                    strategyService.getAvailableStrategiesDescription());
            return;
        }

        log.debug(
                "Импорт файла: {} с стратегией: {}. Количество машин: {}",
                importParcelParams.inputFilePath(),
                strategy.getAlgorithmName(),
                importParcelParams.truckCount());

        ImportParcelProcessor processor = new ImportParcelProcessor(
                importParcelParams,
                importParcelMachineSource,
                fileParcelSource,
                normalizer,
                parcelBuilder,
                stringValidator,
                gridValidator,
                strategy,
                output);

        processor.process();
    }

    /**
     * Парсит строку ввода в параметры команды.
     *
     * @param input строка ввода от пользователя
     * @return объект с параметрами или null в случае ошибки
     */
    @Nullable
    private ImportParcelParams parseParams(@NonNull String input) {
        ImportParcelConsoleCommandParams params = new ImportParcelConsoleCommandParams();
        JCommander jCommander = JCommander.newBuilder().addObject(params).build();
        jCommander.setProgramName(ConsoleCommandType.IMPORT_PARCEL.toString());

        try {
            // Убираем название команды и парсим остаток
            String argsLine = input.trim()
                    .substring(ConsoleCommandType.IMPORT_PARCEL.toString().length())
                    .trim();
            String[] args = argsLine.split("\\s+");

            jCommander.parse(args);

            if (params.isHelp()) {
                printHelp(jCommander);
                return null;
            }

            return new ImportParcelParams(
                    params.getInputFile(), params.getOutputFile(), params.getStrategyId(), params.getTruckCount());
        } catch (ParameterException e) {
            log.error("Ошибка парсинга параметров: {}", e.getMessage());
            printHelp(jCommander);
            return null;
        }
    }

    /**
     * Выводит справку по использованию команды.
     *
     * @param jCommander объект JCommander для получения форматированной справки
     */
    private void printHelp(@NonNull JCommander jCommander) {
        StringBuilder sb = new StringBuilder();
        jCommander.getUsageFormatter().usage(sb);
        log.info(sb.toString());
    }
}
