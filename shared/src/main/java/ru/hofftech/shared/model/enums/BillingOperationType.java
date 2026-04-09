package ru.hofftech.shared.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * Типы операций биллинга.
 * <p>
 * Определяет возможные операции, за которые производится списание средств.
 */
@Getter
@RequiredArgsConstructor
@NullMarked
public enum BillingOperationType {
    LOAD("Погрузка"),
    UNLOAD("Разгрузка");

    private final String description;
}
