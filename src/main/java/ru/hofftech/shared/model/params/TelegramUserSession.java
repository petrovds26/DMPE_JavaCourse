package ru.hofftech.shared.model.params;

import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.TelegramCommandType;

/**
 * Интерфейс для сессий пользователей Telegram.
 */
public interface TelegramUserSession {

    /**
     * Возвращает тип текущей выполняемой команды.
     *
     * @return тип команды или null, если команда не выполняется
     */
    @Nullable
    TelegramCommandType getCurrentCommand();
}
