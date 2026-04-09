package ru.hofftech.telegram.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * Типы Telegram команд, доступных в боте.
 */
@Getter
@NullMarked
@RequiredArgsConstructor
public enum TelegramCommandType {
    START("/start", "Запуск бота"),
    LOAD("/load", "Загрузка посылки в машины"),
    CREATE_PARCEL("/create_parcel", "Создать новую посылку"),
    READ_PARCEL("/read_parcel", "Просмотреть посылку"),
    UPDATE_PARCEL("/update_parcel", "Обновить посылку"),
    DELETE_PARCEL("/delete_parcel", "Удалить посылку"),
    LIST_PARCELS("/list_parcel", "Список всех посылок"),
    READ_BILLING("/read_billing", "История оплаты"),
    CANCEL("/cancel", "Отменить текущую операцию");

    private final String command;

    private final String description;
}
