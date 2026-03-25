package ru.hofftech.createparcel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * Перечисление шагов создания посылки в Telegram.
 * Определяет последовательность ввода данных пользователем.
 */
@Getter
@RequiredArgsConstructor
@NullMarked
public enum CreateParcelTelegramStep {
    ENTER_NAME(1, "Введите название посылки"),
    ENTER_SYMBOL(2, "Введите символ посылки"),
    ENTER_FORM(3, "Введите форму посылки (например: xxx\\nx x\\nxxx)");

    private final int id;

    private final String description;
}
