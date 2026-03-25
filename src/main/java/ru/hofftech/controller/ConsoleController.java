package ru.hofftech.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.slf4j.MDC;
import ru.hofftech.shared.service.command.console.ConsoleCommand;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Контроллер для обработки консольных команд.
 * Отвечает за чтение ввода пользователя и диспетчеризацию команд.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public class ConsoleController {

    private static final String MDC_INPUT_KEY = "input";
    private static final String MDC_COMMAND_NAME_KEY = "commandName";

    private final List<ConsoleCommand> consoleCommands;

    /**
     * Запускает основной цикл обработки консольных команд.
     * Читает ввод пользователя и передаёт команды на выполнение.
     */
    public void listen() {
        var scanner = new Scanner(System.in);

        String commandsInfo = consoleCommands.stream()
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
     * Обрабатывает введённую команду.
     *
     * @param input строка ввода от пользователя
     * @return true если команда найдена и выполнена, false если команда не найдена
     */
    private boolean processCommand(String input) {
        try {
            MDC.put(MDC_INPUT_KEY, input);

            for (ConsoleCommand command : consoleCommands) {
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
