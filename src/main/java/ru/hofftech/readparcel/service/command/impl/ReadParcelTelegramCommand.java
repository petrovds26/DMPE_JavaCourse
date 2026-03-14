package ru.hofftech.readparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hofftech.readparcel.enums.ReadParcelTelegramStep;
import ru.hofftech.readparcel.model.params.ReadParcelTelegramUserSession;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.core.telegram.TelegramCommandResponse;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.telegram.TelegramCommand;
import ru.hofftech.shared.util.TelegramKeyboardUtil;

/**
 * Telegram команда для чтения посылки по названию.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class ReadParcelTelegramCommand implements TelegramCommand {

    @NonNull
    private final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCommandStart(@NonNull String text) {
        return text.equals(getType().getCommand());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public TelegramCommandType getType() {
        return TelegramCommandType.READ_PARCEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(@NonNull Update update, @Nullable TelegramUserSession session) {
        String text = getMessageText(update);
        if (text == null) return false;

        // Команда может обработать, если:
        // 1. Это начало команды
        if (isCommandStart(text)) return true;

        // 2. Это продолжение сессии создания
        return session instanceof ReadParcelTelegramUserSession;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public TelegramCommandResponse execute(@NonNull Update update, @Nullable TelegramUserSession session) {
        String text = getMessageText(update);
        long chatId = getChatId(update);

        if (text == null) {
            return TelegramCommandResponse.text("Ошибка: пустое сообщение");
        }

        // Начало создания посылки
        if (session == null) {
            return startReading(chatId);
        }

        // Продолжение создания
        if (session instanceof ReadParcelTelegramUserSession readSession) {
            return continueReading(readSession, text);
        }

        return TelegramCommandResponse.text("Ошибка: неверное состояние сессии");
    }

    /**
     * Начинает процесс чтения посылки.
     *
     * @param chatId идентификатор чата
     * @return ответ с запросом названия
     */
    @NonNull
    private TelegramCommandResponse startReading(long chatId) {
        ReadParcelTelegramUserSession session = ReadParcelTelegramUserSession.start(chatId);
        return TelegramCommandResponse.startSession(session.getStep().getDescription(), session);
    }

    /**
     * Завершает процесс чтения посылки.
     *
     * @param session текущая сессия
     * @param text название посылки
     * @return ответ с результатом чтения
     */
    @NonNull
    private TelegramCommandResponse continueReading(
            @NonNull ReadParcelTelegramUserSession session, @NonNull String text) {

        if (session.getStep() == ReadParcelTelegramStep.ENTER_NAME) { // Ожидание названия

            ReadParcelProcessorCommand processorCommand = new ReadParcelProcessorCommand(parcelRepository, text);
            ProcessorCommandResult processorCommandResult = processorCommand.execute();

            return TelegramCommandResponse.endSessionWithKeyboard(
                    processorCommandResult.message(), TelegramKeyboardUtil.createCommandsKeyboard());
        }
        return TelegramCommandResponse.text("Ошибка: неверный шаг создания");
    }

    /**
     * Извлекает идентификатор чата из обновления.
     *
     * @param update обновление от Telegram
     * @return идентификатор чата или 0
     */
    private long getChatId(@NonNull Update update) {
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
    private String getMessageText(@NonNull Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }
        return null;
    }
}
