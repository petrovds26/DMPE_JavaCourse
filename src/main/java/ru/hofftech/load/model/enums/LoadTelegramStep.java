package ru.hofftech.load.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

/**
 * Перечисление шагов создания посылки в Telegram.
 * Определяет последовательность ввода данных пользователем.
 */
@Getter
@RequiredArgsConstructor
public enum LoadTelegramStep {
    ENTER_PARCEL(1, "Введите список посылок к погрузке. Каждая посылка с новой строки"),
    ENTER_TRUCK(2, "Введите список машин. Каждая машина с новой строки"),
    ENTER_STRATEGY(3, "Введите номер стратегии"),
    FINISH(4, "Команда выполнена");

    private final int id;

    @NonNull
    private final String description;
}
