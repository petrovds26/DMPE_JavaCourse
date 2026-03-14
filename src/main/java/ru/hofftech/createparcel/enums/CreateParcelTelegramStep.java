package ru.hofftech.createparcel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

/**
 * Перечисление шагов создания посылки в Telegram.
 * Определяет последовательность ввода данных пользователем.
 */
@Getter
@RequiredArgsConstructor
public enum CreateParcelTelegramStep {
    ENTER_NAME(1, "Введите название посылки"),
    ENTER_SYMBOL(2, "Введите символ посылки"),
    ENTER_FORM(3, "Введите форму посылки (например: xxx\\nx x\\nxxx)"),
    FINISH(4, "Команда выполнена");

    private final int id;

    @NonNull
    private final String description;
}
