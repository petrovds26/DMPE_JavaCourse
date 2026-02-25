package ru.hofftech.service.command;

/**
 * Интерфейс для всех консольных команд
 */
public interface ConsoleCommand {
    /**
     * @return название команды
     */
    String getName();

    /**
     * @return описание команды
     */
    String getDescription();

    /**
     * Проверяет, подходит ли данная команда для обработки введённой строки
     * @param input строка ввода от пользователя
     * @return true, если команда может обработать этот ввод
     */
    boolean matches(String input);

    /**
     * Выполняет команду
     * @param input полная строка ввода от пользователя
     */
    void execute(String input);
}
