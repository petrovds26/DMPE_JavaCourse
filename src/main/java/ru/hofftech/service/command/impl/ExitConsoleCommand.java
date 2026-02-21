package ru.hofftech.service.command.impl;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.service.command.ConsoleCommand;
import ru.hofftech.service.command.ConsoleCommandType;

@Slf4j
public class ExitConsoleCommand implements ConsoleCommand {

    @Override
    public String getName() {
        return ConsoleCommandType.EXIT.toString();
    }

    @Override
    public String getDescription() {
        return "Для запуска необходимо ввести: exit";
    }

    @Override
    public boolean matches(String input) {
        return input.trim().equalsIgnoreCase(ConsoleCommandType.EXIT.toString());
    }

    @Override
    public void execute(String input) {
        log.info("Завершение работы программы...");
        System.exit(0);
    }
}
