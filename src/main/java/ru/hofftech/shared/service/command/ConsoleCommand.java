package ru.hofftech.shared.service.command;

import org.jspecify.annotations.NonNull;

/**
 * Интерфейс для всех консольных команд
 */
public interface ConsoleCommand {
    /**
     * @return название команды
     */
    @NonNull
    String getName();

    /**
     * @return описание команды
     */
    @NonNull
    String getDescription();

    /**
     * Проверяет, подходит ли данная команда для обработки введённой строки
     * @param input строка ввода от пользователя
     * @return true, если команда может обработать этот ввод
     */
    boolean matches(@NonNull String input);

    /**
     * Выполняет команду
     * @param input полная строка ввода от пользователя
     */
    void execute(@NonNull String input);
}
