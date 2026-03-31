package ru.hofftech.readparcel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * Перечисление шагов чтения посылки в Telegram.
 */
@Getter
@RequiredArgsConstructor
@NullMarked
public enum ReadParcelTelegramStep {
    ENTER_NAME(1, "Введите название посылки");

    private final int id;

    private final String description;
}
