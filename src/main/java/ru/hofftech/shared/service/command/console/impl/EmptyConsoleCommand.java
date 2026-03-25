package ru.hofftech.shared.service.command.console.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.service.command.console.ConsoleCommand;

/**
 * Консольная команда для обработки пустого ввода.
 * Используется для игнорирования пустых строк в консоли.
 */
@Slf4j
@NullMarked
@RequiredArgsConstructor
public class EmptyConsoleCommand implements ConsoleCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return ConsoleCommandType.EMPTY.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Для запуска передайте пустую строку";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String input) {
        return input.trim().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(String input) {
        log.info("Передана пустая команда");
    }
}
