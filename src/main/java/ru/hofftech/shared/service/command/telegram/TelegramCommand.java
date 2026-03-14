package ru.hofftech.shared.service.command.telegram;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hofftech.shared.model.core.telegram.TelegramCommandResponse;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;

/**
 * Интерфейс для всех Telegram команд
 */
public interface TelegramCommand {

    /**
     * Возвращает тип команды.
     *
     * @return тип команды
     */
    @NonNull
    TelegramCommandType getType();

    /**
     * Проверяет, может ли команда обработать сообщение.
     *
     * @param update обновление от Telegram
     * @param session текущая сессия пользователя
     * @return true если команда может обработать
     */
    boolean canHandle(@NonNull Update update, @Nullable TelegramUserSession session);

    /**
     * Выполняет команду.
     *
     * @param update обновление от Telegram
     * @param session текущая сессия пользователя
     * @return ответ для отправки пользователю
     */
    @NonNull
    TelegramCommandResponse execute(@NonNull Update update, @Nullable TelegramUserSession session);

    /**
     * Проверяет, является ли текст началом команды.
     *
     * @param text текст сообщения
     * @return true если это начало команды
     */
    boolean isCommandStart(@NonNull String text);
}
