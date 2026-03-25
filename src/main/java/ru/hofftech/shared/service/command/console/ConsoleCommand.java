package ru.hofftech.shared.service.command.console;

import org.jspecify.annotations.NullMarked;

/**
 * Интерфейс для всех консольных команд
 */
@NullMarked
public interface ConsoleCommand {

    /**
     * Возвращает название команды.
     *
     * @return название команды
     */
    String getName();

    /**
     * Возвращает описание команды.
     *
     * @return описание команды
     */
    String getDescription();

    /**
     * Проверяет, подходит ли данная команда для обработки введённой строки.
     *
     * @param input строка ввода от пользователя
     * @return true, если команда может обработать этот ввод
     */
    boolean matches(String input);

    /**
     * Выполняет команду.
     *
     * @param input полная строка ввода от пользователя
     */
    void execute(String input);
}
