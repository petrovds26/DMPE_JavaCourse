package ru.hofftech.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import ru.hofftech.service.command.ConsoleCommand;
import ru.hofftech.service.command.impl.EmptyConsoleCommand;
import ru.hofftech.service.command.impl.ExitConsoleCommand;
import ru.hofftech.service.command.impl.ImportParcelConsoleCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ConsoleController {

    private static final String MDC_INPUT_KEY = "input";
    private static final String MDC_COMMAND_NAME_KEY = "commandName";

    private List<ConsoleCommand> commands;

    public void listen() {
        initCommands();
        var scanner = new Scanner(System.in);

        String commandsInfo = commands.stream()
                .map(cmd -> String.format("%n\t[%s] : %s", cmd.getName(), cmd.getDescription()))
                .collect(Collectors.joining());
        log.info("Доступные команды:{}", commandsInfo);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            boolean commandExecuted = processCommand(input);

            // Если ни одна команда не подошла
            if (!commandExecuted) {
                log.debug("Неизвестная команда: {}", input);
            }
        }
    }

    /**
     * Инициализация списка доступных команд
     */
    private void initCommands() {
        commands = new ArrayList<>();
        commands.add(new EmptyConsoleCommand());
        commands.add(new ImportParcelConsoleCommand());
        commands.add(new ExitConsoleCommand());
    }

    /**
     * Обрабатывает введённую команду
     * @param input строка ввода от пользователя
     * @return true если команда найдена и выполнена, false если команда не найдена
     */
    private boolean processCommand(String input) {
        try {
            MDC.put(MDC_INPUT_KEY, input);

            for (ConsoleCommand command : commands) {
                if (command.matches(input)) {
                    try {
                        MDC.put(MDC_COMMAND_NAME_KEY, command.getName());
                        command.execute(input);
                    } catch (Exception e) {
                        log.error("Ошибка при выполнении команды {}: {}", command.getName(), e.getMessage(), e);
                    }
                    return true;
                }
            }

            return false;

        } finally {
            MDC.clear();
        }
    }
}
