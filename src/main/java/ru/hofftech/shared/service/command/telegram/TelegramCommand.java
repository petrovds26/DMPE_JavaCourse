package ru.hofftech.shared.service.command.telegram;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hofftech.shared.model.core.telegram.TelegramCommandResponse;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;

/**
 * Интерфейс для всех Telegram команд
 */
@NullMarked
public interface TelegramCommand {

    /**
     * Возвращает тип команды.
     *
     * @return тип команды
     */
    TelegramCommandType getType();

    /**
     * Проверяет, может ли команда обработать сообщение.
     *
     * @param update обновление от Telegram
     * @param session текущая сессия пользователя
     * @return true если команда может обработать
     */
    boolean canHandle(Update update, @Nullable TelegramUserSession session);

    /**
     * Выполняет команду.
     *
     * @param update обновление от Telegram
     * @param session текущая сессия пользователя
     * @return ответ для отправки пользователю
     */
    TelegramCommandResponse execute(Update update, @Nullable TelegramUserSession session);

    /**
     * Проверяет, является ли текст началом команды.
     *
     * @param text текст сообщения
     * @return true если это начало команды
     */
    boolean isCommandStart(String text);
}
