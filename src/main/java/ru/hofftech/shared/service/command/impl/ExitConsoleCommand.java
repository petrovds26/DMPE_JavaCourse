package ru.hofftech.shared.service.command.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.service.command.ConsoleCommand;
import ru.hofftech.shared.service.command.ConsoleCommandType;

@Slf4j
public class ExitConsoleCommand implements ConsoleCommand {

    @Override
    @NonNull
    public String getName() {
        return ConsoleCommandType.EXIT.toString();
    }

    @Override
    @NonNull
    public String getDescription() {
        return "Для запуска необходимо ввести: exit";
    }

    @Override
    public boolean matches(@NonNull String input) {
        return input.trim().equalsIgnoreCase(ConsoleCommandType.EXIT.toString());
    }

    @Override
    public void execute(@NonNull String input) {
        log.info("Завершение работы программы...");
        System.exit(0);
    }
}
