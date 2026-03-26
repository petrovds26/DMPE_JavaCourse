package ru.hofftech.controller;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hofftech.shared.model.core.telegram.TelegramCommandResponse;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;
import ru.hofftech.shared.service.command.telegram.TelegramCommand;
import ru.hofftech.shared.service.telegram.TelegramUserSessionService;

import java.util.List;

/**
 * Контроллер для обработки Telegram сообщений.
 * Отвечает за приём сообщений от Telegram API и диспетчеризацию команд.
 */
@Slf4j
@NullMarked
public class TelegramController extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "7234493703:AAHdghQRLDQrUCMGr92rHz6k4Qlxhdcbsyc";
    private static final String BOT_USERNAME = "@PetrovDS26_SimpleTest_bot";

    private final List<TelegramCommand<? extends TelegramUserSession>> commands;

    private final TelegramUserSessionService sessionService;

    /**
     * Создаёт новый контроллер Telegram бота.
     *
     * @param commands список доступных команд
     * @param sessionService сервис управления сессиями пользователей
     */
    public TelegramController(List<TelegramCommand<?>> commands, TelegramUserSessionService sessionService) {
        super(BOT_TOKEN);
        this.commands = commands;
        this.sessionService = sessionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            // Диспетчеризация команды
            TelegramCommandResponse response = dispatch(update);

            if (response == null) {
                return;
            }

            long chatId = getChatId(update);

            // Обновление сессии
            if (response.newSession() != null) {
                sessionService.createOrUpdateSession(chatId, response.newSession());
            } else if (response.clearSession()) {
                sessionService.clearSession(chatId);
            }

            // Отправка ответа
            sendMessage(toSendMessage(chatId, response));

        } catch (Exception e) {
            log.error("Ошибка при обработке обновления", e);
            sendErrorMessage(update);
        }
    }

    /**
     * Диспетчеризует входящее обновление к соответствующей команде.
     *
     * @param update входящее обновление от Telegram
     * @return ответ команды или null, если команда не найдена
     */
    @Nullable
    public TelegramCommandResponse dispatch(Update update) {
        long chatId = getChatId(update);
        String text = getMessageText(update);

        if (text == null) {
            return null;
        }

        // Получаем текущую сессию
        TelegramUserSession session = sessionService.getSession(chatId);

        // Проверяем команду отмены
        if (text.equalsIgnoreCase(TelegramCommandType.CANCEL.getCommand())) {
            TelegramCommand<? extends TelegramUserSession> cancelCommand = findCommand(TelegramCommandType.CANCEL);
            if (cancelCommand == null) {
                return TelegramCommandResponse.text("Не определена команда Отмена. Введите /help для справки.");
            }
            return executeCommand(cancelCommand, update, session);
        }

        // Если есть сессия, ищем команду по ней
        if (session != null) {
            TelegramCommandType currentCommandType = session.getCurrentCommand();
            if (currentCommandType != null) {
                TelegramCommand<? extends TelegramUserSession> command = findCommand(currentCommandType);
                if (command == null) {
                    return TelegramCommandResponse.text("Не определена команда в сессии. Введите /help для справки.");
                }
                return executeCommand(command, update, session);
            }
        }

        // Если нет сессии, ищем команду по тексту
        TelegramCommandType commandType = TelegramCommandType.fromString(text);
        if (commandType != null) {
            TelegramCommand<? extends TelegramUserSession> command = findCommand(commandType);
            if (command == null) {
                return TelegramCommandResponse.text("Неизвестная команда. Введите /help для справки.");
            }
            return executeCommand(command, update, session);
        }

        return TelegramCommandResponse.text("Неизвестная команда. Введите /help для справки.");
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private <T extends TelegramUserSession> TelegramCommandResponse executeCommand(
            TelegramCommand<? extends TelegramUserSession> command, Update update, @Nullable TelegramUserSession session) {
        TelegramCommand<T> typedCommand = (TelegramCommand<T>) command;
        T typedSession = (T) session;

        if (typedCommand.canHandle(update, typedSession)) {
            return typedCommand.execute(update, typedSession);
        }
        return null;
    }

    /**
     * Находит команду по её типу.
     *
     * @param commandType тип команды
     * @return команда или null, если не найдена
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends TelegramUserSession> TelegramCommand<T> findCommand(TelegramCommandType commandType) {
        return (TelegramCommand<T>) commands.stream()
                .filter(c -> c.getType().equals(commandType))
                .findFirst()
                .orElse(null);
    }

    /**
     * Преобразует ответ команды в сообщение Telegram.
     *
     * @param chatId идентификатор чата
     * @param response ответ команды
     * @return готовое сообщение для отправки
     */
    private SendMessage toSendMessage(long chatId, TelegramCommandResponse response) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(response.text());
        if (response.keyboard() != null) {
            message.setReplyMarkup(response.keyboard());
        }
        return message;
    }

    /**
     * Извлекает идентификатор чата из обновления.
     *
     * @param update обновление от Telegram
     * @return идентификатор чата или 0, если не удалось определить
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
     * Отправляет сообщение об ошибке.
     *
     * @param update обновление, вызвавшее ошибку
     */
    private void sendErrorMessage(Update update) {
        long chatId = getChatId(update);
        if (chatId != 0) {
            sendMessage(SendMessage.builder()
                    .chatId(chatId)
                    .text("Произошла внутренняя ошибка. Попробуйте позже.")
                    .build());
        }
    }

    /**
     * Отправляет сообщение в Telegram.
     *
     * @param message сообщение для отправки
     */
    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }

    /**
     * Извлекает текст сообщения из обновления.
     *
     * @param update обновление от Telegram
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
