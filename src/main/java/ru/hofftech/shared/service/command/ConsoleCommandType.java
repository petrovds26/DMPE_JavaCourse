package ru.hofftech.shared.service.command;

import org.jspecify.annotations.NonNull;

public enum ConsoleCommandType {
    EMPTY,
    IMPORT_PARCEL,
    IMPORT_MACHINE,
    EXIT;

    @Override
    @NonNull
    public String toString() {
        return name().toLowerCase();
    }
}
