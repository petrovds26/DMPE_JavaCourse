package ru.hofftech.telegram.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * Состояния диалога в Telegram боте.
 * <p>
 * Каждое состояние соответствует определённому шагу в многошаговом диалоге с пользователем
 * и содержит описание того, что должен ввести пользователь.
 */
@Getter
@RequiredArgsConstructor
@NullMarked
public enum State {
    INITIAL("Дефолтное состояние"),
    NAME("Введите название посылки"),
    SYMBOL("Введите символ посылки (один символ)"),
    FORM("Введите форму посылки (например: xxx\nx x\nxxx)"),
    USER_ID("Введите id пользователя"),
    FROM_DATE("Введите дату начала в формате дд.мм.гггг"),
    TO_DATE("Введите дату конца в формате дд.мм.гггг"),
    PARCELS("Введите названия посылок (каждая посылка с новой строки)"),
    MACHINES("Введите формы машин (каждая машина с новой строки)"),
    STRATEGY("Введите идентификатор стратегии");

    private final String description;
}
