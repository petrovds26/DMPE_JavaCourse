package ru.hofftech.readparcel.model.params;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.model.params.ConsoleCommandParams;

/**
 * Параметры для команды чтения посылок в консоли.
 */
@Getter
@ToString
@Parameters(commandDescription = "Чтение посылок")
@RequiredArgsConstructor
public class ReadParcelConsoleCommandParams implements ConsoleCommandParams {

    @Parameter(
            names = {"--name", "-n"},
            description = "Название посылки",
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
    public @NonNull ConsoleCommandType getCommandType() {
        return ConsoleCommandType.READ_PARCEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHelp() {
        return help;
    }
}
