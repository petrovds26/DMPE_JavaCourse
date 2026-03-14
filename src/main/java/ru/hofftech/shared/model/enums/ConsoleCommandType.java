package ru.hofftech.shared.model.enums;

import org.jspecify.annotations.NonNull;

/**
 * Типы консольных команд.
 */
public enum ConsoleCommandType {
    EMPTY,
    IMPORT_PARCEL,
    IMPORT_MACHINE,
    CREATE_PARCEL,
    READ_PARCEL,
    UPDATE_PARCEL,
    DELETE_PARCEL,
    EXIT;

    /**
     * Возвращает строковое представление команды в нижнем регистре.
     *
     * @return название команды
     */
    @Override
    @NonNull
    public String toString() {
        return name().toLowerCase();
    }
}
