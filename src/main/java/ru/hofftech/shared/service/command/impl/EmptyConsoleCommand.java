package ru.hofftech.shared.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.shared.service.command.ConsoleCommand;
import ru.hofftech.shared.service.command.ConsoleCommandType;

@Slf4j
@RequiredArgsConstructor
public class EmptyConsoleCommand implements ConsoleCommand {

    @Override
    public String getName() {
        return ConsoleCommandType.EMPTY.toString();
    }

    @Override
    public String getDescription() {
        return "Для запуска передайте пустую строку";
    }

    @Override
    public boolean matches(String input) {
        return input.trim().isEmpty();
    }

    @Override
    public void execute(String input) {
        log.info("Передана пустая команда");
    }
}
