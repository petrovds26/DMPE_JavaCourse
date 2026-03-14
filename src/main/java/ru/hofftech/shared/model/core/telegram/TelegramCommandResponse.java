package ru.hofftech.shared.model.core.telegram;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.hofftech.shared.model.params.TelegramUserSession;

/**
 * Ответ от команды Telegram.
 *
 * @param text текст ответа
 * @param keyboard клавиатура для отображения
 * @param newSession новая сессия пользователя
 * @param clearSession нужно ли очистить сессию
 */
@Builder
public record TelegramCommandResponse(
        @NonNull String text,
        @Nullable ReplyKeyboardMarkup keyboard,
        @Nullable TelegramUserSession newSession,
        boolean clearSession) {

    /**
     * Создаёт простой текстовый ответ.
     *
     * @param text текст ответа
     * @return ответ команды
     */
    @NonNull
    public static TelegramCommandResponse text(@NonNull String text) {
        return TelegramCommandResponse.builder().text(text).clearSession(false).build();
    }

    /**
     * Создаёт ответ с началом сессии.
     *
     * @param text текст ответа
     * @param session новая сессия
     * @return ответ команды
     */
    @NonNull
    public static TelegramCommandResponse startSession(@NonNull String text, @NonNull TelegramUserSession session) {
        return TelegramCommandResponse.builder()
                .text(text)
                .newSession(session)
                .clearSession(false)
                .build();
    }

    /**
     * Создаёт ответ с завершением сессии и клавиатурой.
     *
     * @param text текст ответа
     * @param keyboard клавиатура
     * @return ответ команды
     */
    @NonNull
    public static TelegramCommandResponse endSessionWithKeyboard(
            @NonNull String text, @NonNull ReplyKeyboardMarkup keyboard) {
        return TelegramCommandResponse.builder()
                .text(text)
                .keyboard(keyboard)
                .clearSession(true)
                .build();
    }
}
