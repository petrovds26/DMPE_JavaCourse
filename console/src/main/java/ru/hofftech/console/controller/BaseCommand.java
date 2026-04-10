package ru.hofftech.console.controller;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.console.exception.ConsoleException;
import ru.hofftech.console.exception.FeignException;
import ru.hofftech.console.exception.JsonUtilException;
import ru.hofftech.console.exception.ValidateException;

/**
 * Базовый класс для всех консольных команд.
 * <p>
 * Предоставляет единый механизм обработки ошибок для всех команд.
 * При возникновении исключения формируется понятное пользователю сообщение.
 */
@NullMarked
@Slf4j
public abstract class BaseCommand {

    /**
     * Выполняет команду с обработкой ошибок.
     * <p>
     * Перехватывает различные типы исключений и преобразует их
     * в сообщения, понятные конечному пользователю.
     *
     * @param executor выполняемая команда
     * @return результат выполнения команды или сообщение об ошибке
     */
    protected String executeWithErrorHandling(CommandExecutor executor) {
        try {
            return executor.execute();
        } catch (IllegalArgumentException | ValidateException e) {
            log.error("Ошибка валидации: {}", e.getMessage());
            return String.format("Ошибка валидации: %s", e.getMessage());
        } catch (ConsoleException | JsonUtilException e) {
            log.error("Ошибка при работе алгоритмов: {}", e.getMessage());
            return String.format("Ошибка при работе алгоритмов: %s", e.getMessage());
        } catch (FeignException e) {
            log.error("Ошибка при вызове Core сервиса", e);
            return String.format("Ошибка сервера: %s", e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка", e);
            return String.format("Произошла ошибка: %s", e.getMessage());
        }
    }

    /**
     * Функциональный интерфейс для выполнения команды.
     */
    @FunctionalInterface
    protected interface CommandExecutor {
        /**
         * Выполняет команду.
         *
         * @return результат выполнения
         */
        String execute();
    }
}
