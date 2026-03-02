package ru.hofftech.importmachine.service.command.impl;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.importmachine.model.params.ImportMachineConsoleCommandParams;
import ru.hofftech.importmachine.model.params.ImportMachineParams;
import ru.hofftech.importmachine.service.ImportMachineProcessor;
import ru.hofftech.importmachine.service.output.ImportMachineOutput;
import ru.hofftech.importmachine.service.output.ImportMachineOutputService;
import ru.hofftech.importmachine.service.parser.source.ImportMachineFileSource;
import ru.hofftech.importmachine.service.parser.source.ImportMachineFileSourceService;
import ru.hofftech.shared.service.command.ConsoleCommand;
import ru.hofftech.shared.service.command.ConsoleCommandType;
import ru.hofftech.shared.util.FileTypeUtil;
import ru.hofftech.shared.validation.impl.InputFilePathValidator;
import ru.hofftech.shared.validation.impl.OutputFilePathValidator;

import java.util.List;

@Slf4j
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class ImportMachineConsoleCommand implements ConsoleCommand {

    private final ImportMachineFileSourceService importMachineFileSourceService;
    private final ImportMachineOutputService importMachineOutputService;
    private final InputFilePathValidator inputFilePathValidator;
    private final OutputFilePathValidator outputFilePathValidator;

    public ImportMachineConsoleCommand() {
        this.importMachineFileSourceService = new ImportMachineFileSourceService();
        this.importMachineOutputService = new ImportMachineOutputService();
        this.inputFilePathValidator = new InputFilePathValidator();
        this.outputFilePathValidator = new OutputFilePathValidator();
    }

    @Override
    public String getName() {
        return ConsoleCommandType.IMPORT_MACHINE.toString();
    }

    @Override
    public String getDescription() {
        return "Импорт посылок из файла. Поддерживаются форматы .json. " + "Используйте --help для справки.";
    }

    @Override
    public boolean matches(String input) {
        return input.trim().startsWith(ConsoleCommandType.IMPORT_MACHINE + " ");
    }

    @Override
    public void execute(String input) {

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

        ImportMachineFileSource<String> fileMachineSource = importMachineFileSourceService.getSourceByFileType(
                FileTypeUtil.fromFilename(importMachineParams.inputFilePath()));

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

        ImportMachineOutput output = importMachineOutputService.getOutputByFileType(
                FileTypeUtil.fromFilename(importMachineParams.outputFilePath()));

        if (output == null) {
            log.error(
                    "Не удалось определить тип исходящего файла. Поддерживаются форматы {}. Проверьте исходящий файл: {}",
                    importMachineOutputService.getAvailableFileExtensionDescription(),
                    importMachineParams.outputFilePath());
            return;
        }
        log.debug("Импорт файла: {}", importMachineParams.inputFilePath());

        ImportMachineProcessor processor = new ImportMachineProcessor(importMachineParams, fileMachineSource, output);

        processor.process();
    }

    private ImportMachineParams parseParams(String input) {
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

    private void printHelp(JCommander jCommander) {
        StringBuilder sb = new StringBuilder();
        jCommander.getUsageFormatter().usage(sb);
        log.info(sb.toString());
    }
}
