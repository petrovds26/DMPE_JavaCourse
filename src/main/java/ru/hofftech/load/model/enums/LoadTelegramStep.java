package ru.hofftech.load.model.enums;

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
public enum LoadTelegramStep {
    ENTER_PARCEL(1, "Введите список посылок к погрузке. Каждая посылка с новой строки"),
    ENTER_TRUCK(2, "Введите список машин. Каждая машина с новой строки"),
    ENTER_STRATEGY(3, "Введите номер стратегии"),
    FINISH(4, "Команда выполнена");

    private final int id;

    private final String description;
}
