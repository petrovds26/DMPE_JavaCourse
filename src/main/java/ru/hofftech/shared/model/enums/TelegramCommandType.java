package ru.hofftech.shared.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Типы Telegram команд.
 * Определяет все доступные команды в Telegram интерфейсе.
 */
@Getter
@NullMarked
@RequiredArgsConstructor
public enum TelegramCommandType {
    LOAD("/load", "Загрузка посылки в машины"),
    CREATE_PARCEL("/create_parcel", "Создать новую посылку"),
    READ_PARCEL("/read_parcel", "Просмотреть посылку"),
    UPDATE_PARCEL("/update_parcel", "Обновить посылку"),
    DELETE_PARCEL("/delete_parcel", "Удалить посылку"),
    LIST_PARCELS("/list_parcel", "Список всех посылок"),
    CANCEL("/cancel", "Отменить текущую операцию");

    private final String command;

    private final String description;

    /**
     * Получает тип команды по её текстовому представлению.
     *
     * @param text текст команды (не может быть null)
     * @return тип команды или null, если не найдена
     */
    public static @Nullable TelegramCommandType fromString(String text) {
        for (TelegramCommandType cmd : values()) {
            if (cmd.command.equalsIgnoreCase(text.trim())) {
                return cmd;
            }
        }
        return null;
    }
}
