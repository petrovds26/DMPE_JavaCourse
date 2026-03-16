package ru.hofftech.shared.model.enums;

import org.jspecify.annotations.NonNull;

/**
 * Типы консольных команд.
 * Определяет все доступные команды в консольном интерфейсе.
 */
public enum ConsoleCommandType {
    EMPTY,
    LOAD,
    IMPORT_MACHINE,
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
    @NonNull
    public String toString() {
        return name().toLowerCase();
    }
}
