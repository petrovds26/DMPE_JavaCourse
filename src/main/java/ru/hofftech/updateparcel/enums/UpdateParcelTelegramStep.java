package ru.hofftech.updateparcel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * Перечисление шагов обновления посылки в Telegram.
 */
@Getter
@NullMarked
@RequiredArgsConstructor
public enum UpdateParcelTelegramStep {
    ENTER_NAME(1, "Введите название посылки"),
    ENTER_SYMBOL(2, "Введите символ посылки"),
    ENTER_FORM(3, "Введите форму посылки (например: xxx\\nx x\\nxxx)"),
    FINISH(4, "Команда выполнена");

    private final int id;

    private final String description;
}
