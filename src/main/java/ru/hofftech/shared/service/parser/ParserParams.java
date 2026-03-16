package ru.hofftech.shared.service.parser;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.params.ConsoleCommandParams;
import ru.hofftech.shared.util.StringUtil;

/**
 * Парсер параметров командной строки для консольных команд.
 * Использует JCommander для разбора аргументов.
 */
@Slf4j
public class ParserParams {

    /**
     * Парсит командную строку и заполняет параметры.
     *
     * @param params      объект с параметрами команды (не может быть null)
     * @param commandLine полная командная строка (не может быть null)
     * @return true если парсинг успешен, false при ошибке или запросе help
     */
    public boolean parserCommandLine(@NonNull ConsoleCommandParams params, @NonNull String commandLine) {
        JCommander jCommander = JCommander.newBuilder().addObject(params).build();
        jCommander.setProgramName(params.getCommandType().toString());

        try {
            // Убираем название команды и парсим остаток
            String argsLine = commandLine
                    .trim()
                    .substring(params.getCommandType().toString().length())
                    .trim();
            String[] args = StringUtil.splitCommandLine(argsLine);

            jCommander.parse(args);

            if (params.isHelp()) {
                printHelp(jCommander);
                return false;
            }

            return true;
        } catch (ParameterException e) {
            log.error("Ошибка парсинга параметров: {}", e.getMessage());
            printHelp(jCommander);
            return false;
        }
    }

    /**
     * Выводит справку по использованию команды.
     *
     * @param jCommander объект JCommander (не может быть null)
     */
    private void printHelp(@NonNull JCommander jCommander) {
        StringBuilder sb = new StringBuilder();
        jCommander.getUsageFormatter().usage(sb);
        log.info(sb.toString());
    }
}
