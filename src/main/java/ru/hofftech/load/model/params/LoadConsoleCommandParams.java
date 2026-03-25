package ru.hofftech.load.model.params;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.validators.PositiveInteger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.model.params.ConsoleCommandParams;

/**
 * Параметры для команды удаления посылок в консоли.
 */
@Getter
@ToString
@Parameters(commandDescription = "Загрузка посылок в машину")
@RequiredArgsConstructor
@NullMarked
public class LoadConsoleCommandParams implements ConsoleCommandParams {

    @Parameter(
            names = {"--parcelsFile", "-pf"},
            description = "Входной файл (поддерживаются .txt и .enums)",
            order = 0)
    @Nullable
    private String inputParcelFile;

    @Parameter(
            names = {"--parcelsText", "-pt"},
            description = "Посылки текстом",
            order = 1)
    @Nullable
    private String inputParcelText;

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
            names = {"--strategy", "-s"},
            description = "Стратегия упаковки",
            required = true,
            validateWith = PositiveInteger.class,
            order = 4)
    @Nullable
    private Integer strategyId;

    @Parameter(
            names = {"--trucks", "-t"},
            description = "Описание доступных машин",
            required = true,
            order = 5)
    @Nullable
    private String truck;

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
        return ConsoleCommandType.LOAD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHelp() {
        return help;
    }

    public String getInputParcelFile() {
        if (inputParcelFile == null) {
            return "";
        }
        return inputParcelFile;
    }

    public String getInputParcelText() {
        if (inputParcelText == null) {
            return "";
        }
        return inputParcelText;
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

    public Integer getStrategyId() {
        if (strategyId == null) {
            return 0;
        }
        return strategyId;
    }

    public String getTruck() {
        if (truck == null) {
            return "";
        }
        return truck;
    }
}
