package ru.hofftech.unload.model.params;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.model.params.ConsoleCommandParams;

/**
 * Параметры для команды импорта посылок
 */
@Getter
@ToString
@Parameters(commandDescription = "Импорт машин из файла")
@RequiredArgsConstructor
public class UnloadConsoleCommandParams implements ConsoleCommandParams {

    @Parameter(
            names = {"--input", "-i"},
            description = "Входной файл (поддерживается .json)",
            required = true,
            order = 0)
    private String inputFile;

    @Parameter(
            names = {"--outputFile", "-of"},
            description = "Выходной файл для сохранения результата",
            order = 2)
    private String outputFile;

    @Parameter(
            names = {"--output", "-o"},
            description = "Тип вывода результата",
            required = true,
            order = 3)
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
    public @NonNull ConsoleCommandType getCommandType() {
        return ConsoleCommandType.UNLOAD;
    }
}
