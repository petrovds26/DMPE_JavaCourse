package ru.hofftech.deleteparcel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

/**
 * Перечисление шагов удаления посылки в Telegram.
 */
@Getter
@RequiredArgsConstructor
public enum DeleteParcelTelegramStep {
    ENTER_NAME(1, "Введите название посылки");

    private final int id;

    @NonNull
    private final String description;
}
