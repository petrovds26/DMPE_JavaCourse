package ru.hofftech.shared.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для работы со строками.
 */
@UtilityClass
public class StringUtil {

    /**
     * Разбивает командную строку на аргументы с учётом кавычек.
     *
     * @param commandLine командная строка
     * @return массив аргументов
     */
    @NonNull
    public static String[] splitCommandLine(@NonNull String commandLine) {
        List<String> args = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false;
        int i = 0;

        while (i < commandLine.length()) {
            char c = commandLine.charAt(i);

            if (c == '"' && (i == 0 || commandLine.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
                i++;
            } else if (c == ' ' && !inQuotes) {
                if (!currentArg.isEmpty()) {
                    args.add(currentArg.toString());
                    currentArg = new StringBuilder();
                }
                i++;
            } else if (c == '\\' && i + 1 < commandLine.length() && commandLine.charAt(i + 1) == '"') {
                // Экранированная кавычка
                currentArg.append('"');
                i += 2; // Пропускаем обратный слеш и кавычку
            } else {
                currentArg.append(c);
                i++;
            }
        }

        if (!currentArg.isEmpty()) {
            args.add(currentArg.toString());
        }

        return args.toArray(new String[0]);
    }
}
