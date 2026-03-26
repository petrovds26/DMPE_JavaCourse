package ru.hofftech.load.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hofftech.load.model.enums.LoadTelegramStep;
import ru.hofftech.load.model.params.LoadProcessorCommandParams;
import ru.hofftech.load.model.params.LoadTelegramUserSession;
import ru.hofftech.load.service.loader.strategy.LoadStrategy;
import ru.hofftech.load.service.loader.strategy.LoadStrategyService;
import ru.hofftech.load.service.output.LoadPrepareOutputResult;
import ru.hofftech.shared.model.core.ParserMachineProcessorResult;
import ru.hofftech.shared.model.core.ParserParcelProcessorResult;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.model.core.telegram.TelegramCommandResponse;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.service.command.telegram.TelegramCommand;
import ru.hofftech.shared.service.parser.ParserMachineProcessor;
import ru.hofftech.shared.service.parser.ParserParcelProcessor;
import ru.hofftech.shared.util.TelegramKeyboardUtil;

import java.util.List;

/**
 * Telegram команда для создания новой посылки.
 * Реализует пошаговый диалог с пользователем.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public class LoadParcelTelegramCommand implements TelegramCommand<LoadTelegramUserSession> {

    private final ParserParcelProcessor<String> parserParcelProcessor;

    private final ParserMachineProcessor<String> parserMachineProcessor;

    private final LoadStrategyService strategyService;

    private final LoadPrepareOutputResult loadPrepareOutputResult;

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
        return TelegramCommandType.LOAD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(Update update, @Nullable LoadTelegramUserSession session) {
        String text = getMessageText(update);
        if (text == null) return false;

        // Команда может обработать, если:
        // 1. Это начало команды
        if (isCommandStart(text)) return true;

        // 2. Это продолжение сессии создания
        return session != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TelegramCommandResponse execute(Update update, @Nullable LoadTelegramUserSession session) {
        String text = getMessageText(update);
        long chatId = getChatId(update);

        if (text == null) {
            return TelegramCommandResponse.text("Ошибка: пустое сообщение");
        }

        // Начало создания посылки
        if (session == null) {
            return startLoad(chatId);
        }

        // Продолжение создания
        return continueLoad(session, text);
    }

    /**
     * Начинает процесс создания посылки.
     *
     * @param chatId идентификатор чата
     * @return ответ с запросом названия посылки
     */
    private TelegramCommandResponse startLoad(long chatId) {
        LoadTelegramUserSession session = LoadTelegramUserSession.start(chatId);
        return TelegramCommandResponse.startSession(session.getStep().getDescription(), session);
    }

    /**
     * Продолжает процесс создания посылки на текущем шаге.
     *
     * @param session текущая сессия
     * @param text введённый пользователем текст
     * @return ответ со следующим шагом или результат создания
     */
    private TelegramCommandResponse continueLoad(LoadTelegramUserSession session, String text) {

        return switch (session.getStep()) {
            case ENTER_PARCEL -> handleParcelInput(session, text);
            case ENTER_TRUCK -> handleTruckInput(session, text);
            case ENTER_STRATEGY -> handleStrategyInput(session, text);
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
    private TelegramCommandResponse handleParcelInput(LoadTelegramUserSession session, String text) {

        ParserParcelProcessorResult parcelResult = parserParcelProcessor.transform(text);
        if (parcelResult.hasErrors()) {
            return TelegramCommandResponse.text(parcelResult.getErrorsAsString());
        }
        LoadTelegramUserSession parcelSession =
                session.withParcels(parcelResult.parcels()).withStep(LoadTelegramStep.ENTER_TRUCK);
        return TelegramCommandResponse.startSession(parcelSession.getStep().getDescription(), parcelSession);
    }

    /**
     * Обрабатывает ввод символа посылки.
     *
     * @param session текущая сессия
     * @param text введённый символ
     * @return ответ с запросом формы
     */
    private TelegramCommandResponse handleTruckInput(LoadTelegramUserSession session, String text) {
        ParserMachineProcessorResult truckResult = parserMachineProcessor.transform(text);
        if (truckResult.hasErrors()) {
            return TelegramCommandResponse.text(truckResult.getErrorsAsString());
        }
        LoadTelegramUserSession formSession =
                session.withMachines(truckResult.machines()).withStep(LoadTelegramStep.ENTER_STRATEGY);
        return TelegramCommandResponse.startSession(formSession.getStep().getDescription(), formSession);
    }

    /**
     * Обрабатывает ввод формы посылки и создаёт её.
     *
     * @param session текущая сессия
     * @param text введённая форма
     * @return ответ с результатом создания
     */
    private TelegramCommandResponse handleStrategyInput(LoadTelegramUserSession session, String text) {
        if (session.getParcels() == null || session.getMachines() == null) {
            return TelegramCommandResponse.text("Не указаны обязательные параметры.");
        }

        int id;

        try {
            id = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return TelegramCommandResponse.text(String.format(
                    "Некорректное значение стратегии. Нужно использовать число. %s",
                    strategyService.getAvailableStrategiesDescription()));
        }

        // Парсинг стратегии
        LoadStrategy strategy = strategyService.getStrategyById(id);
        if (strategy == null) {
            return TelegramCommandResponse.text(String.format(
                    "Стратегия с ID %d. не найдена. Доступные ID: %s",
                    id, strategyService.getAvailableStrategiesDescription()));
        }

        LoadProcessorCommandParams loadProcessorCommandParams = LoadProcessorCommandParams.builder()
                .parcels(session.getParcels())
                .machines(session.getMachines())
                .prevErrors(List.of())
                .build();

        LoadParcelProcessorCommand processorCommand = new LoadParcelProcessorCommand(strategy, loadPrepareOutputResult);

        ProcessorCommandResult result = processorCommand.execute(loadProcessorCommandParams);

        return TelegramCommandResponse.endSessionWithKeyboard(
                result.message(), TelegramKeyboardUtil.createCommandsKeyboard());
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
