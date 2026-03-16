package ru.hofftech.shared.service.command.console.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.enums.ConsoleCommandType;
import ru.hofftech.shared.service.command.console.ConsoleCommand;

/**
 * Консольная команда для обработки пустого ввода.
 * Используется для игнорирования пустых строк в консоли.
 */
@Slf4j
@RequiredArgsConstructor
public class EmptyConsoleCommand implements ConsoleCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getName() {
        return ConsoleCommandType.EMPTY.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getDescription() {
        return "Для запуска передайте пустую строку";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@NonNull String input) {
        return input.trim().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NonNull String input) {
        log.info("Передана пустая команда");
    }
}
