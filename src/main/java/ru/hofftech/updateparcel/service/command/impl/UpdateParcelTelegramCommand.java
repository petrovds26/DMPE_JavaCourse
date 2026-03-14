package ru.hofftech.updateparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.core.TransformParcelResult;
import ru.hofftech.shared.model.core.telegram.TelegramCommandResponse;
import ru.hofftech.shared.model.dto.ParcelFormDto;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.telegram.TelegramCommand;
import ru.hofftech.shared.service.parser.ParserParcelProcessor;
import ru.hofftech.shared.util.TelegramKeyboardUtil;
import ru.hofftech.updateparcel.enums.UpdateParcelTelegramStep;
import ru.hofftech.updateparcel.model.params.UpdateParcelTelegramUserSession;

/**
 * Telegram команда для обновления посылки.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class UpdateParcelTelegramCommand implements TelegramCommand {

    @NonNull
    private final ParcelRepository parcelRepository;

    @NonNull
    private final ParserParcelProcessor parserParcelProcessor;

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
        return TelegramCommandType.UPDATE_PARCEL;
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
        return session instanceof UpdateParcelTelegramUserSession;
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
     * @return ответ с запросом названия
     */
    @NonNull
    private TelegramCommandResponse startUpd(long chatId) {
        UpdateParcelTelegramUserSession session = UpdateParcelTelegramUserSession.start(chatId);
        return TelegramCommandResponse.startSession(session.getStep().getDescription(), session);
    }

    /**
     * Продолжает процесс обновления посылки.
     *
     * @param session текущая сессия
     * @param text введённый текст
     * @return ответ со следующим шагом или результат обновления
     */
    @NonNull
    private TelegramCommandResponse continueUpd(
            @NonNull UpdateParcelTelegramUserSession session, @NonNull String text) {

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
     * @param session текущая сессия
     * @param text введённое название
     * @return ответ с запросом символа
     */
    @NonNull
    private TelegramCommandResponse handleNameInput(
            @NonNull UpdateParcelTelegramUserSession session, @NonNull String text) {
        String errorValidateNameResult =
                parserParcelProcessor.validateName(text).error();
        if (errorValidateNameResult != null) {
            return TelegramCommandResponse.text(errorValidateNameResult);
        }

        UpdateParcelTelegramUserSession nameSession =
                session.withName(text).withStep(UpdateParcelTelegramStep.ENTER_SYMBOL);
        return TelegramCommandResponse.startSession(nameSession.getStep().getDescription(), nameSession);
    }

    /**
     * Обрабатывает ввод символа посылки.
     *
     * @param session текущая сессия
     * @param text введённый символ
     * @return ответ с запросом формы
     */
    @NonNull
    private TelegramCommandResponse handleSymbolInput(
            @NonNull UpdateParcelTelegramUserSession session, @NonNull String text) {
        String errorValidateSymbolResult =
                parserParcelProcessor.validateSymbol(text).error();
        if (errorValidateSymbolResult != null) {
            return TelegramCommandResponse.text(errorValidateSymbolResult);
        }
        UpdateParcelTelegramUserSession formSession =
                session.withSymbol(text).withStep(UpdateParcelTelegramStep.ENTER_FORM);
        return TelegramCommandResponse.startSession(formSession.getStep().getDescription(), formSession);
    }

    /**
     * Обрабатывает ввод формы и обновляет посылку.
     *
     * @param session текущая сессия
     * @param text введённая форма
     * @return ответ с результатом обновления
     */
    @NonNull
    private TelegramCommandResponse handleFormInput(
            @NonNull UpdateParcelTelegramUserSession session, @NonNull String text) {
        if (session.getName() == null || session.getSymbol() == null) {
            return TelegramCommandResponse.text("Не указаны обязательные параметры.");
        }

        ParcelFormDto parcelFormDto = ParcelFormDto.builder()
                .form(text)
                .name(session.getName())
                .symbol(session.getSymbol())
                .build();

        TransformParcelResult transformResult = parserParcelProcessor.transform(parcelFormDto);

        if (transformResult.parcel() == null) {
            String errorTransformResult = transformResult.error();
            if (errorTransformResult == null) {
                errorTransformResult = "Не удалось распознать посылку";
            }
            return TelegramCommandResponse.text(errorTransformResult);
        }

        UpdateParcelProcessorCommand command =
                new UpdateParcelProcessorCommand(parcelRepository, transformResult.parcel());

        ProcessorCommandResult result = command.execute();

        return TelegramCommandResponse.endSessionWithKeyboard(
                result.message(), TelegramKeyboardUtil.createCommandsKeyboard());
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
