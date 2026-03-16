package ru.hofftech.createparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hofftech.createparcel.enums.CreateParcelTelegramStep;
import ru.hofftech.createparcel.model.params.CreateParcelTelegramUserSession;
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

import java.util.List;

/**
 * Telegram команда для создания новой посылки.
 * Реализует пошаговый диалог с пользователем.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class CreateParcelTelegramCommand implements TelegramCommand {

    @NonNull
    private final ParcelRepository parcelRepository;

    @NonNull
    private final ParserParcelFromFormDto parserParcelProcessor;

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
        return TelegramCommandType.CREATE_PARCEL;
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
        return session instanceof CreateParcelTelegramUserSession;
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
            return startCreation(chatId);
        }

        // Продолжение создания
        if (session instanceof CreateParcelTelegramUserSession createSession) {
            return continueCreation(createSession, text);
        }

        return TelegramCommandResponse.text("Ошибка: неверное состояние сессии");
    }

    /**
     * Начинает процесс создания посылки.
     *
     * @param chatId идентификатор чата
     * @return ответ с запросом названия посылки
     */
    @NonNull
    private TelegramCommandResponse startCreation(long chatId) {
        CreateParcelTelegramUserSession session = CreateParcelTelegramUserSession.start(chatId);
        return TelegramCommandResponse.startSession(session.getStep().getDescription(), session);
    }

    /**
     * Продолжает процесс создания посылки на текущем шаге.
     *
     * @param session текущая сессия
     * @param text введённый пользователем текст
     * @return ответ со следующим шагом или результат создания
     */
    @NonNull
    private TelegramCommandResponse continueCreation(
            @NonNull CreateParcelTelegramUserSession session, @NonNull String text) {

        return switch (session.getStep()) {
            case ENTER_NAME -> handleNameInput(session, text);
            case ENTER_SYMBOL -> handleSymbolInput(session, text);
            case ENTER_FORM -> handleFormInput(session, text);
            default -> TelegramCommandResponse.text("Ошибка: неверный шаг создания");
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
            @NonNull CreateParcelTelegramUserSession session, @NonNull String text) {
        ParserParcelProcessorResult nameResult = parserParcelProcessor.validateName(text);
        if (nameResult.hasErrors()) {
            return TelegramCommandResponse.text(nameResult.getErrorsAsString());
        }

        CreateParcelTelegramUserSession nameSession =
                session.withName(text).withStep(CreateParcelTelegramStep.ENTER_SYMBOL);
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
            @NonNull CreateParcelTelegramUserSession session, @NonNull String text) {
        ParserParcelProcessorResult symbolResult = parserParcelProcessor.validateSymbol(text);
        if (symbolResult.hasErrors()) {
            return TelegramCommandResponse.text(symbolResult.getErrorsAsString());
        }
        CreateParcelTelegramUserSession formSession =
                session.withSymbol(text).withStep(CreateParcelTelegramStep.ENTER_FORM);
        return TelegramCommandResponse.startSession(formSession.getStep().getDescription(), formSession);
    }

    /**
     * Обрабатывает ввод формы посылки и создаёт её.
     *
     * @param session текущая сессия
     * @param text введённая форма
     * @return ответ с результатом создания
     */
    @NonNull
    private TelegramCommandResponse handleFormInput(
            @NonNull CreateParcelTelegramUserSession session, @NonNull String text) {
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

        CreateParcelProcessorCommand command = new CreateParcelProcessorCommand(parcelRepository);

        ProcessorCommandResult result =
                command.execute(processorResult.parcels().getFirst());

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
