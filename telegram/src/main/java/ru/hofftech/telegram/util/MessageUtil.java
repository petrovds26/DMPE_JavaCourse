package ru.hofftech.telegram.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hofftech.telegram.model.enums.TelegramCommandType;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для генерации сообщений бота.
 */
@UtilityClass
@NullMarked
public class MessageUtil {

    /**
     * Формирует приветственное сообщение для пользователя.
     *
     * @param firstName имя пользователя
     * @return отформатированное приветственное сообщение со списком доступных команд
     */
    public static String getWelcomeMessage(String firstName) {
        return "Привет, %s!%nЯ бот для управления посылками.%nДоступные команды:%n%s"
                .formatted(firstName, getCommandsHelp());
    }

    /**
     * Формирует сообщение со списком всех доступных команд.
     *
     * @return строка со списком команд, где каждая команда на новой строке
     */
    public static String getCommandsHelp() {
        return Arrays.stream(TelegramCommandType.values())
                .filter(cmd -> cmd != TelegramCommandType.START) // START уже в приветствии
                .map(cmd -> String.format("%s - %s", cmd.getCommand(), cmd.getDescription()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Формирует сообщение о неизвестной команде.
     *
     * @return сообщение с подсказкой о вводе /start для получения списка команд
     */
    public static String getUnknownCommandMessage() {
        return "Неизвестная команда.%nВведите %s для списка доступных команд."
                .formatted(TelegramCommandType.START.getCommand());
    }

    /**
     * Создаёт сообщение для отправки пользователю.
     *
     * @param chatId идентификатор чата получателя
     * @param message текст сообщения
     * @param cancelKeyboard флаг, определяющий необходимость отображения клавиатуры с кнопкой "Отмена"
     * @return готовое к отправке сообщение SendMessage
     */
    public static SendMessage createSendMessage(Long chatId, String message, boolean cancelKeyboard) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .replyMarkup(
                        cancelKeyboard
                                ? TelegramKeyboardUtil.createCancelKeyboard()
                                : TelegramKeyboardUtil.createCommandsKeyboard())
                .build();
    }
}
