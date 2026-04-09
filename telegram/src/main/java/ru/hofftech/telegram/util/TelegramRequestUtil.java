package ru.hofftech.telegram.util;

import io.github.drednote.telegram.core.request.DefaultTelegramRequest;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.telegram.model.enums.TelegramCommandType;

import java.util.Set;

/**
 * Утилитарный класс для создания запросов к Telegram API.
 * <p>
 * Предоставляет методы для создания типовых запросов: команд, текстовых сообщений и callback-запросов.
 */
@UtilityClass
@NullMarked
public class TelegramRequestUtil {

    /**
     * Создаёт запрос для обработки команды по её типу.
     *
     * @param commandType тип команды
     * @return объект DefaultTelegramRequest для команды
     */
    public static DefaultTelegramRequest createCommandRequest(TelegramCommandType commandType) {
        return new DefaultTelegramRequest(
                Set.of(commandType.getCommand()), Set.of(RequestType.MESSAGE), Set.of(MessageType.COMMAND), false);
    }

    /**
     * Создаёт запрос для обработки команды по её строковому представлению.
     *
     * @param command строка команды (например, "/start")
     * @return объект DefaultTelegramRequest для команды
     */
    public static DefaultTelegramRequest createCommandRequest(String command) {
        return new DefaultTelegramRequest(
                Set.of(command), Set.of(RequestType.MESSAGE), Set.of(MessageType.COMMAND), false);
    }

    /**
     * Создаёт запрос для обработки любого текстового сообщения.
     *
     * @return объект DefaultTelegramRequest для текстовых сообщений
     */
    public static DefaultTelegramRequest createTextRequest() {
        return new DefaultTelegramRequest(Set.of("**"), Set.of(RequestType.MESSAGE), Set.of(MessageType.TEXT), false);
    }

    /**
     * Создаёт запрос для обработки callback-запроса.
     *
     * @param pattern паттерн для сопоставления с данными callback-запроса
     * @return объект DefaultTelegramRequest для callback-запроса
     */
    public static DefaultTelegramRequest createCallbackRequest(String pattern) {
        return new DefaultTelegramRequest(Set.of(pattern), Set.of(RequestType.CALLBACK_QUERY), Set.of(), false);
    }
}
