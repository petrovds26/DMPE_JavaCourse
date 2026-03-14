package ru.hofftech.shared.service.command.console.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.service.command.console.ConsoleCommand;

/**
 * Консольная команда для выхода из приложения.
 */
@Slf4j
public class ExitConsoleCommand implements ConsoleCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getName() {
        return ConsoleCommandType.EXIT.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getDescription() {
        return "Для запуска необходимо ввести: exit";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@NonNull String input) {
        return input.trim().equalsIgnoreCase(ConsoleCommandType.EXIT.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NonNull String input) {
        log.info("Завершение работы программы...");
        System.exit(0);
    }
}
