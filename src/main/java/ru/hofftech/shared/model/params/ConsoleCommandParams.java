package ru.hofftech.shared.model.params;

import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.enums.ConsoleCommandType;

/**
 * Интерфейс для параметров консольных команд.
 * Определяет общие методы для всех DTO параметров команд.
 */
@NullMarked
public interface ConsoleCommandParams {
    /**
     * Возвращает тип команды.
     *
     * @return тип команды (не может быть null)
     */
    ConsoleCommandType getCommandType();

    /**
     * Проверяет, была ли запрошена справка.
     *
     * @return true если запрошена справка
     */
    boolean isHelp();
}
