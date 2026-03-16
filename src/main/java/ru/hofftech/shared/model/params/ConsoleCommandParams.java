package ru.hofftech.shared.model.params;

import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.enums.ConsoleCommandType;

/**
 * Интерфейс для параметров консольных команд.
 * Определяет общие методы для всех DTO параметров команд.
 */
public interface ConsoleCommandParams {
    /**
     * Возвращает тип команды.
     *
     * @return тип команды (не может быть null)
     */
    @NonNull
    ConsoleCommandType getCommandType();

    /**
     * Проверяет, была ли запрошена справка.
     *
     * @return true если запрошена справка
     */
    boolean isHelp();
}
