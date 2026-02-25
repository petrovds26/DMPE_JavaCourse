package ru.hofftech.service.command;

public enum ConsoleCommandType {
    EMPTY,
    IMPORT,
    EXIT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
