package ru.hofftech.telegram.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.hofftech.telegram.model.enums.TelegramCommandType;

import java.util.List;

/**
 * Утилитарный класс для создания клавиатур Telegram.
 */
@UtilityClass
@NullMarked
public class TelegramKeyboardUtil {

    /**
     * Создаёт основную клавиатуру с командами бота.
     *
     * @return клавиатура с кнопками основных команд
     */
    public static ReplyKeyboardMarkup createCommandsKeyboard() {
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(
                        createRow(
                                TelegramCommandType.CREATE_PARCEL,
                                TelegramCommandType.READ_PARCEL,
                                TelegramCommandType.UPDATE_PARCEL),
                        createRow(
                                TelegramCommandType.DELETE_PARCEL,
                                TelegramCommandType.LIST_PARCELS,
                                TelegramCommandType.LOAD),
                        createRow(TelegramCommandType.READ_BILLING)))
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    /**
     * Создаёт клавиатуру с единственной кнопкой "Отмена".
     *
     * @return клавиатура с кнопкой отмены, которая скрывается после нажатия
     */
    public static ReplyKeyboardMarkup createCancelKeyboard() {
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(createRow(TelegramCommandType.CANCEL)))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }

    /**
     * Создаёт строку клавиатуры из указанных команд.
     *
     * @param commands команды, для которых создаются кнопки
     * @return строка клавиатуры с кнопками команд
     */
    private static KeyboardRow createRow(TelegramCommandType... commands) {
        KeyboardRow row = new KeyboardRow();
        for (TelegramCommandType cmd : commands) {
            row.add(cmd.getCommand());
        }
        return row;
    }
}
