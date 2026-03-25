package ru.hofftech.shared.service.command.telegram.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hofftech.shared.model.core.telegram.TelegramCommandResponse;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;
import ru.hofftech.shared.service.command.telegram.TelegramCommand;
import ru.hofftech.shared.util.TelegramKeyboardUtil;

/**
 * Telegram команда для отмены текущей операции.
 * Очищает сессию пользователя и возвращает главное меню.
 */
@NullMarked
@Slf4j
public class CancelTelegramCommand implements TelegramCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    public TelegramCommandType getType() {
        return TelegramCommandType.CANCEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCommandStart(String text) {
        return text.equals(getType().getCommand());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(Update update, @Nullable TelegramUserSession session) {
        String text = getMessageText(update);
        return text != null && isCommandStart(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TelegramCommandResponse execute(Update update, @Nullable TelegramUserSession session) {
        return TelegramCommandResponse.endSessionWithKeyboard(
                "Операция отменена. Выберите команду:", TelegramKeyboardUtil.createCommandsKeyboard());
    }

    /**
     * Извлекает текст сообщения из обновления.
     *
     * @param update обновление от Telegram (не может быть null)
     * @return текст сообщения или null, если его нет
     */
    @Nullable
    private String getMessageText(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }
        return null;
    }
}
