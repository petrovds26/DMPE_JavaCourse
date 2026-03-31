package ru.hofftech.readparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.core.telegram.TelegramCommandResponse;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSessionDefault;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.telegram.TelegramCommand;
import ru.hofftech.shared.util.TelegramKeyboardUtil;

/**
 * Telegram команда для получения списка всех посылок.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public class ReadAllParcelTelegramCommand implements TelegramCommand<TelegramUserSessionDefault> {

    private final ParcelRepository parcelRepository;

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
    public TelegramCommandType getType() {
        return TelegramCommandType.LIST_PARCELS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(Update update, @Nullable TelegramUserSessionDefault session) {
        String text = getMessageText(update);
        if (text == null) return false;

        // Команда может обработать, если:
        // 1. Это начало команды
        return isCommandStart(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TelegramCommandResponse execute(Update update, @Nullable TelegramUserSessionDefault session) {
        // Начало создания посылки
        if (session == null) {
            ReadParcelProcessorCommand processorCommand = new ReadParcelProcessorCommand(parcelRepository);
            ProcessorCommandResult processorCommandResult = processorCommand.execute(null);

            return TelegramCommandResponse.endSessionWithKeyboard(
                    processorCommandResult.message(), TelegramKeyboardUtil.createCommandsKeyboard());
        }

        return TelegramCommandResponse.text("Ошибка: неверное состояние сессии");
    }

    /**
     * Извлекает текст сообщения из обновления.
     *
     * @param update обновление от Telegram
     * @return текст сообщения или null
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
