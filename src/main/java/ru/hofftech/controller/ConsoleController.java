package ru.hofftech.controller;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import ru.hofftech.importmachine.service.command.impl.ImportMachineConsoleCommand;
import ru.hofftech.importparcel.service.command.impl.ImportParcelConsoleCommand;
import ru.hofftech.shared.service.command.ConsoleCommand;
import ru.hofftech.shared.service.command.impl.EmptyConsoleCommand;
import ru.hofftech.shared.service.command.impl.ExitConsoleCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class ConsoleController {

    private static final String MDC_INPUT_KEY = "input";
    private static final String MDC_COMMAND_NAME_KEY = "commandName";

    @NonNull
    private final List<ConsoleCommand> commands = new ArrayList<>(List.of(
            new EmptyConsoleCommand(),
            new ImportParcelConsoleCommand(),
            new ImportMachineConsoleCommand(),
            new ExitConsoleCommand()));

    public void listen() {
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
     * Обрабатывает введённую команду
     * @param input строка ввода от пользователя
     * @return true если команда найдена и выполнена, false если команда не найдена
     */
    private boolean processCommand(@NonNull String input) {
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
