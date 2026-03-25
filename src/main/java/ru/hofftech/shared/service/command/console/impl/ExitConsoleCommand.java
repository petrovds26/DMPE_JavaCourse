package ru.hofftech.shared.service.command.console.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.service.command.console.ConsoleCommand;

/**
 * Консольная команда для выхода из приложения.
 * Завершает работу программы с кодом 0.
 */
@Slf4j
@NullMarked
public class ExitConsoleCommand implements ConsoleCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return ConsoleCommandType.EXIT.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Для запуска необходимо ввести: exit";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String input) {
        return input.trim().equalsIgnoreCase(ConsoleCommandType.EXIT.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(String input) {
        log.info("Завершение работы программы...");
        System.exit(0);
    }
}
