package ru.hofftech.shared.model.enums;

import org.jspecify.annotations.NullMarked;

/**
 * Типы консольных команд.
 * Определяет все доступные команды в консольном интерфейсе.
 */
@NullMarked
public enum ConsoleCommandType {
    EMPTY,
    LOAD,
    UNLOAD,
    CREATE_PARCEL,
    READ_PARCEL,
    UPDATE_PARCEL,
    DELETE_PARCEL,
    EXIT;

    /**
     * Возвращает строковое представление команды в нижнем регистре.
     *
     * @return название команды (не может быть null)
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
