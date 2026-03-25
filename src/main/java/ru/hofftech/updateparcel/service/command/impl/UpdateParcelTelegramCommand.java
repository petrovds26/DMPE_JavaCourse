package ru.hofftech.updateparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hofftech.shared.model.core.ParserParcelProcessorResult;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.core.telegram.TelegramCommandResponse;
import ru.hofftech.shared.model.dto.ParcelFormDto;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.telegram.TelegramCommand;
import ru.hofftech.shared.service.parser.impl.ParserParcelFromFormDto;
import ru.hofftech.shared.util.TelegramKeyboardUtil;
import ru.hofftech.updateparcel.enums.UpdateParcelTelegramStep;
import ru.hofftech.updateparcel.model.params.UpdateParcelTelegramUserSession;

import java.util.List;

/**
 * Telegram команда для обновления посылки.
 * Реализует пошаговый диалог с пользователем для обновления существующей посылки.
 */
@Slf4j
@NullMarked
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class UpdateParcelTelegramCommand implements TelegramCommand {

    private final ParcelRepository parcelRepository;

    private final ParserParcelFromFormDto parserParcelProcessor;

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
        return TelegramCommandType.UPDATE_PARCEL;
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
        return session instanceof UpdateParcelTelegramUserSession;
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
            return startUpd(chatId);
        }

        // Продолжение создания
        if (session instanceof UpdateParcelTelegramUserSession updateSession) {
            return continueUpd(updateSession, text);
        }

        return TelegramCommandResponse.text("Ошибка: неверное состояние сессии");
    }

    /**
     * Начинает процесс обновления посылки.
     *
     * @param chatId идентификатор чата
     * @return ответ с запросом названия (не может быть null)
     */
    private TelegramCommandResponse startUpd(long chatId) {
        UpdateParcelTelegramUserSession session = UpdateParcelTelegramUserSession.start(chatId);
        return TelegramCommandResponse.startSession(session.getStep().getDescription(), session);
    }

    /**
     * Продолжает процесс обновления посылки.
     *
     * @param session текущая сессия (не может быть null)
     * @param text    введённый текст (не может быть null)
     * @return ответ со следующим шагом или результат обновления (не может быть null)
     */
    private TelegramCommandResponse continueUpd(UpdateParcelTelegramUserSession session, String text) {

        return switch (session.getStep()) {
            case ENTER_NAME -> handleNameInput(session, text);
            case ENTER_SYMBOL -> handleSymbolInput(session, text);
            case ENTER_FORM -> handleFormInput(session, text);
            default -> TelegramCommandResponse.text("Ошибка: неверный шаг обновления посылки");
        };
    }

    /**
     * Обрабатывает ввод названия посылки.
     *
     * @param session текущая сессия (не может быть null)
     * @param text    введённое название (не может быть null)
     * @return ответ с запросом символа (не может быть null)
     */
    private TelegramCommandResponse handleNameInput(UpdateParcelTelegramUserSession session, String text) {
        ParserParcelProcessorResult nameResult = parserParcelProcessor.validateName(text);
        if (nameResult.hasErrors()) {
            return TelegramCommandResponse.text(nameResult.getErrorsAsString());
        }

        UpdateParcelTelegramUserSession nameSession =
                session.withName(text).withStep(UpdateParcelTelegramStep.ENTER_SYMBOL);
        return TelegramCommandResponse.startSession(nameSession.getStep().getDescription(), nameSession);
    }

    /**
     * Обрабатывает ввод символа посылки.
     *
     * @param session текущая сессия (не может быть null)
     * @param text    введённый символ (не может быть null)
     * @return ответ с запросом формы (не может быть null)
     */
    private TelegramCommandResponse handleSymbolInput(UpdateParcelTelegramUserSession session, String text) {
        ParserParcelProcessorResult symbolResult = parserParcelProcessor.validateSymbol(text);
        if (symbolResult.hasErrors()) {
            return TelegramCommandResponse.text(symbolResult.getErrorsAsString());
        }
        UpdateParcelTelegramUserSession formSession =
                session.withSymbol(text).withStep(UpdateParcelTelegramStep.ENTER_FORM);
        return TelegramCommandResponse.startSession(formSession.getStep().getDescription(), formSession);
    }

    /**
     * Обрабатывает ввод формы и обновляет посылку.
     *
     * @param session текущая сессия (не может быть null)
     * @param text    введённая форма (не может быть null)
     * @return ответ с результатом обновления (не может быть null)
     */
    private TelegramCommandResponse handleFormInput(UpdateParcelTelegramUserSession session, String text) {
        if (session.getName() == null || session.getSymbol() == null) {
            return TelegramCommandResponse.text("Не указаны обязательные параметры.");
        }

        ParcelFormDto parcelFormDto = ParcelFormDto.builder()
                .form(text)
                .name(session.getName())
                .symbol(session.getSymbol())
                .build();

        ParserParcelProcessorResult processorResult = parserParcelProcessor.transform(List.of(parcelFormDto));

        if (processorResult.parcels().isEmpty()) {
            return TelegramCommandResponse.text(
                    processorResult.getErrorsAsString().isBlank()
                            ? "Не удалось распознать посылку"
                            : processorResult.getErrorsAsString());
        }

        UpdateParcelProcessorCommand command = new UpdateParcelProcessorCommand(parcelRepository);

        ProcessorCommandResult result =
                command.execute(processorResult.parcels().getFirst());

        return TelegramCommandResponse.endSessionWithKeyboard(
                result.message(), TelegramKeyboardUtil.createCommandsKeyboard());
    }

    /**
     * Извлекает идентификатор чата из обновления.
     *
     * @param update обновление от Telegram (не может быть null)
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
     * @param update обновление от Telegram (не может быть null)
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
