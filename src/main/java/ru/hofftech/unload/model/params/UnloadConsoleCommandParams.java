package ru.hofftech.unload.model.params;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.model.params.ConsoleCommandParams;

/**
 * Параметры для команды импорта посылок
 */
@Getter
@ToString
@NullMarked
@Parameters(commandDescription = "Импорт машин из файла")
@RequiredArgsConstructor
public class UnloadConsoleCommandParams implements ConsoleCommandParams {

    @Parameter(
            names = {"--input", "-i"},
            description = "Входной файл (поддерживается .json)",
            required = true,
            order = 0)
    @Nullable
    private String inputFile;

    @Parameter(
            names = {"--outputFile", "-of"},
            description = "Выходной файл для сохранения результата",
            order = 2)
    @Nullable
    private String outputFile;

    @Parameter(
            names = {"--output", "-o"},
            description = "Тип вывода результата",
            required = true,
            order = 3)
    @Nullable
    private String outputType;

    @Parameter(
            names = {"--help", "-h"},
            description = "Показать справку",
            help = true,
            order = 4)
    private boolean help;

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsoleCommandType getCommandType() {
        return ConsoleCommandType.UNLOAD;
    }

    public String getInputFile() {
        if (inputFile == null) {
            return "";
        }
        return inputFile;
    }

    public String getOutputFile() {
        if (outputFile == null) {
            return "";
        }
        return outputFile;
    }

    public String getOutputType() {
        if (outputType == null) {
            return "";
        }
        return outputType;
    }
}
