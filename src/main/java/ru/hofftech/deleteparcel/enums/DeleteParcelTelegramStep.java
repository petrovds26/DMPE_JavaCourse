package ru.hofftech.deleteparcel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * Перечисление шагов удаления посылки в Telegram.
 */
@Getter
@RequiredArgsConstructor
@NullMarked
public enum DeleteParcelTelegramStep {
    ENTER_NAME(1, "Введите название посылки");

    private final int id;

    private final String description;
}
