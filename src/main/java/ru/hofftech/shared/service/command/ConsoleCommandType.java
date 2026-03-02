package ru.hofftech.shared.service.command;

public enum ConsoleCommandType {
    EMPTY,
    IMPORT_PARCEL,
    IMPORT_MACHINE,
    EXIT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
