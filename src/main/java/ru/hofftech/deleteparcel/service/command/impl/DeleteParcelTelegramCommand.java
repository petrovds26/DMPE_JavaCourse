package ru.hofftech.deleteparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hofftech.deleteparcel.enums.DeleteParcelTelegramStep;
import ru.hofftech.deleteparcel.model.params.DeleteParcelTelegramUserSession;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.core.telegram.TelegramCommandResponse;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.telegram.TelegramCommand;
import ru.hofftech.shared.util.TelegramKeyboardUtil;

/**
 * Telegram команда для удаления посылки.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public class DeleteParcelTelegramCommand implements TelegramCommand {

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
        return TelegramCommandType.DELETE_PARCEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(Update update, @Nullable TelegramUserSession session) {
        String text = getMessageText(update);
        if (text == null) return false;

        // Команда может обработать, если:
        // 1. Это начало команды
        if (isCommandStart(text)) return true;

        // 2. Это продолжение сессии создания
        return session instanceof DeleteParcelTelegramUserSession;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TelegramCommandResponse execute(Update update, @Nullable TelegramUserSession session) {
        String text = getMessageText(update);
        long chatId = getChatId(update);

        if (text == null) {
            return TelegramCommandResponse.text("Ошибка: пустое сообщение");
        }

        // Начало создания посылки
        if (session == null) {
            return startDelete(chatId);
        }

        // Продолжение создания
        if (session instanceof DeleteParcelTelegramUserSession deleteSession) {
            return continueDelete(deleteSession, text);
        }

        return TelegramCommandResponse.text("Ошибка: неверное состояние сессии");
    }

    /**
     * Начинает процесс удаления посылки.
     *
     * @param chatId идентификатор чата
     * @return ответ с запросом названия
     */
    private TelegramCommandResponse startDelete(long chatId) {
        DeleteParcelTelegramUserSession session = DeleteParcelTelegramUserSession.start(chatId);
        return TelegramCommandResponse.startSession(session.getStep().getDescription(), session);
    }

    /**
     * Завершает процесс удаления посылки.
     *
     * @param session текущая сессия
     * @param text название посылки
     * @return ответ с результатом удаления
     */
    private TelegramCommandResponse continueDelete(DeleteParcelTelegramUserSession session, String text) {

        if (session.getStep() == DeleteParcelTelegramStep.ENTER_NAME) { // Ожидание названия

            DeleteParcelProcessorCommand processorCommand = new DeleteParcelProcessorCommand(parcelRepository);
            ProcessorCommandResult processorCommandResult = processorCommand.execute(text);

            return TelegramCommandResponse.endSessionWithKeyboard(
                    processorCommandResult.message(), TelegramKeyboardUtil.createCommandsKeyboard());
        }
        return TelegramCommandResponse.text("Ошибка: неверный шаг удаления");
    }

    /**
     * Извлекает идентификатор чата из обновления.
     *
     * @param update обновление от Telegram
     * @return идентификатор чата или 0
     */
    private long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return 0;
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
