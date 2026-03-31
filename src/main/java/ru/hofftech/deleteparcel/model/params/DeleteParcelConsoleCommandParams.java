package ru.hofftech.deleteparcel.model.params;

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
 * Параметры для команды удаления посылок в консоли.
 */
@Getter
@ToString
@Parameters(commandDescription = "Создание посылок")
@RequiredArgsConstructor
@NullMarked
public class DeleteParcelConsoleCommandParams implements ConsoleCommandParams {

    @Parameter(
            names = {"--name", "-n"},
            description = "Название посылки",
            required = true,
            order = 0)
    @Nullable
    private String name;

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
        return ConsoleCommandType.DELETE_PARCEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHelp() {
        return help;
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }
}
