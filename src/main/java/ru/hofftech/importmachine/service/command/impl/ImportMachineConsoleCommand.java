package ru.hofftech.importmachine.service.command.impl;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importmachine.model.params.ImportMachineConsoleCommandParams;
import ru.hofftech.importmachine.model.params.ImportMachineParams;
import ru.hofftech.importmachine.service.ImportMachineProcessor;
import ru.hofftech.importmachine.service.output.ImportMachineOutput;
import ru.hofftech.importmachine.service.output.ImportMachineOutputService;
import ru.hofftech.importmachine.service.parser.source.ImportMachineFileSource;
import ru.hofftech.importmachine.service.parser.source.ImportMachineFileSourceService;
import ru.hofftech.shared.model.enums.FileType;
import ru.hofftech.shared.service.command.ConsoleCommand;
import ru.hofftech.shared.service.command.ConsoleCommandType;
import ru.hofftech.shared.util.FileTypeUtil;
import ru.hofftech.shared.validation.impl.InputFilePathValidator;
import ru.hofftech.shared.validation.impl.OutputFilePathValidator;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class ImportMachineConsoleCommand implements ConsoleCommand {
    @NonNull
    private final ImportMachineFileSourceService importMachineFileSourceService;

    @NonNull
    private final ImportMachineOutputService importMachineOutputService;

    @NonNull
    private final InputFilePathValidator inputFilePathValidator;

    @NonNull
    private final OutputFilePathValidator outputFilePathValidator;

    public ImportMachineConsoleCommand() {
        this.importMachineFileSourceService = new ImportMachineFileSourceService();
        this.importMachineOutputService = new ImportMachineOutputService();
        this.inputFilePathValidator = new InputFilePathValidator();
        this.outputFilePathValidator = new OutputFilePathValidator();
    }

    @Override
    @NonNull
    public String getName() {
        return ConsoleCommandType.IMPORT_MACHINE.toString();
    }

    @Override
    @NonNull
    public String getDescription() {
        return "Импорт посылок из файла. Поддерживаются форматы .json. " + "Используйте --help для справки.";
    }

    @Override
    public boolean matches(@NonNull String input) {
        return input.trim().startsWith(ConsoleCommandType.IMPORT_MACHINE + " ");
    }

    @Override
    public void execute(@NonNull String input) {

        ImportMachineParams importMachineParams = parseParams(input);

        if (importMachineParams == null) {
            return;
        }

        // Валидация входных параметров
        List<String> inputErrors = inputFilePathValidator.validate(importMachineParams.inputFilePath());
        if (!inputErrors.isEmpty()) {
            log.error("Ошибки валидации параметра Входной файл: {}", String.join("; ", inputErrors));
            return;
        }
        FileType inputFileType = FileTypeUtil.fromFilename(importMachineParams.inputFilePath());
        if (inputFileType == null) {
            log.error("Ошибки валидации параметра Входной файл: Тип файла не определен");
            return;
        }

        ImportMachineFileSource<String> fileMachineSource =
                importMachineFileSourceService.getSourceByFileType(inputFileType);

        if (fileMachineSource == null) {
            log.error(
                    "Не удалось определить тип входящего файла. Поддерживаются форматы {}. Проверьте входной файл: {}",
                    importMachineFileSourceService.getAvailableFileExtensionDescription(),
                    importMachineParams.inputFilePath());
            return;
        }

        if (importMachineParams.outputFilePath() != null) {
            List<String> outputErrors = outputFilePathValidator.validate(importMachineParams.outputFilePath());
            if (!outputErrors.isEmpty()) {
                log.error("Ошибки валидации параметра Выходной файл: {}", String.join("; ", outputErrors));
                return;
            }
        }

        FileType fileTypeOutput = FileTypeUtil.fromFilename(importMachineParams.outputFilePath());

        if (fileTypeOutput == null
                && importMachineParams.outputFilePath() != null
                && !importMachineParams.outputFilePath().isEmpty()) {
            log.error(
                    "Не удалось определить тип исходящего файла. Поддерживаются форматы {}. Проверьте исходящий файл: {}",
                    importMachineOutputService.getAvailableFileExtensionDescription(),
                    importMachineParams.outputFilePath());
            return;
        }

        Optional<ImportMachineOutput> outputOptional = importMachineOutputService.getOutputByFileType(fileTypeOutput);

        if (outputOptional.isEmpty()) {
            log.error(
                    "Не удалось определить тип исходящего файла. Поддерживаются форматы {}. Проверьте исходящий файл: {}",
                    importMachineOutputService.getAvailableFileExtensionDescription(),
                    importMachineParams.outputFilePath());
            return;
        }
        log.debug("Импорт файла: {}", importMachineParams.inputFilePath());

        ImportMachineProcessor processor =
                new ImportMachineProcessor(importMachineParams, fileMachineSource, outputOptional.get());

        processor.process();
    }

    @Nullable
    private ImportMachineParams parseParams(@NonNull String input) {
        ImportMachineConsoleCommandParams params = new ImportMachineConsoleCommandParams();
        JCommander jCommander = JCommander.newBuilder().addObject(params).build();
        jCommander.setProgramName(ConsoleCommandType.IMPORT_MACHINE.toString());

        try {
            // Убираем название команды и парсим остаток
            String argsLine = input.trim()
                    .substring(ConsoleCommandType.IMPORT_MACHINE.toString().length())
                    .trim();
            String[] args = argsLine.split("\\s+");

            jCommander.parse(args);

            if (params.isHelp()) {
                printHelp(jCommander);
                return null;
            }

            return new ImportMachineParams(params.getInputFile(), params.getOutputFile());
        } catch (ParameterException e) {
            log.error("Ошибка парсинга параметров: {}", e.getMessage());
            printHelp(jCommander);
            return null;
        }
    }

    private void printHelp(@NonNull JCommander jCommander) {
        StringBuilder sb = new StringBuilder();
        jCommander.getUsageFormatter().usage(sb);
        log.info(sb.toString());
    }
}
