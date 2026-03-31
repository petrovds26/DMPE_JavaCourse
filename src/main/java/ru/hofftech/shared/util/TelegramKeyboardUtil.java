package ru.hofftech.shared.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.hofftech.shared.model.enums.TelegramCommandType;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для создания клавиатур Telegram.
 * Предоставляет методы для создания различных типов клавиатур.
 */
@NullMarked
@UtilityClass
public class TelegramKeyboardUtil {

    /**
     * Создаёт клавиатуру с основными командами.
     * Клавиатура содержит кнопки для всех основных операций.
     *
     * @return клавиатура с командами (не может быть null)
     */
    public static ReplyKeyboardMarkup createCommandsKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строка кнопок
        KeyboardRow row1 = new KeyboardRow();
        row1.add(TelegramCommandType.CREATE_PARCEL.getCommand());
        row1.add(TelegramCommandType.READ_PARCEL.getCommand());

        // Вторая строка
        KeyboardRow row2 = new KeyboardRow();
        row2.add(TelegramCommandType.UPDATE_PARCEL.getCommand());
        row2.add(TelegramCommandType.DELETE_PARCEL.getCommand());

        // Третья строка
        KeyboardRow row3 = new KeyboardRow();
        row3.add(TelegramCommandType.LIST_PARCELS.getCommand());
        row3.add(TelegramCommandType.CANCEL.getCommand());

        // Четвертая строка
        KeyboardRow row4 = new KeyboardRow();
        row4.add(TelegramCommandType.LOAD.getCommand());

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
