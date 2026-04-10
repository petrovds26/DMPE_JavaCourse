package ru.hofftech.console.controller;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;
import ru.hofftech.console.service.ParcelService;

/**
 * Консольная команда для управления посылками.
 * <p>
 * Поддерживает операции: создание, обновление, удаление, чтение и список всех посылок.
 */
@NullMarked
@RequiredArgsConstructor
@Component
@Command(command = "parcel", description = "Команды для управления посылками")
public class ParcelCommand extends BaseCommand {
    private final ParcelService parcelService;

    /**
     * Создаёт новую посылку.
     *
     * @param name   название посылки
     * @param form   форма посылки
     * @param symbol символ посылки
     * @return результат операции
     */
    @Command(command = "create", description = "Создать новую посылку")
    public String create(
            @Option(longNames = "name", shortNames = 'n', description = "Название посылки", required = true)
                    String name,
            @Option(longNames = "form", shortNames = 'f', description = "Форма посылки", required = true) String form,
            @Option(longNames = "symbol", shortNames = 's', description = "Символ посылки", defaultValue = "*")
                    String symbol) {
        return executeWithErrorHandling(() -> parcelService.createParcel(name, form, symbol));
    }

    /**
     * Обновляет существующую посылку.
     *
     * @param name   название посылки
     * @param form   новая форма посылки
     * @param symbol новый символ посылки
     * @return результат операции
     */
    @Command(command = "update", description = "Обновить посылку")
    public String update(
            @Option(longNames = "name", shortNames = 'n', description = "Название посылки", required = true)
                    String name,
            @Option(longNames = "form", shortNames = 'f', description = "Форма посылки", required = true) String form,
            @Option(longNames = "symbol", shortNames = 's', description = "Символ посылки", defaultValue = "*")
                    String symbol) {
        return executeWithErrorHandling(() -> parcelService.updateParcel(name, form, symbol));
    }

    /**
     * Удаляет посылку по названию.
     *
     * @param name название посылки
     * @return результат операции
     */
    @Command(command = "delete", description = "Удалить посылку")
    public String delete(
            @Option(longNames = "name", shortNames = 'n', description = "Название посылки", required = true)
                    String name) {
        return executeWithErrorHandling(() -> parcelService.deleteParcel(name));
    }

    /**
     * Показывает информацию о посылке по названию.
     *
     * @param name название посылки
     * @return отформатированная информация о посылке
     */
    @Command(command = "read", description = "Посмотреть посылку")
    public String read(
            @Option(longNames = "name", shortNames = 'n', description = "Название посылки", required = true)
                    String name) {
        return executeWithErrorHandling(() -> parcelService.readParcel(name));
    }

    /**
     * Показывает список всех посылок.
     *
     * @param page     номер страницы для пагинации (опционально)
     * @return отформатированный список всех посылок
     */
    @Command(command = "readAll", description = "Посмотреть все посылки")
    public String readAll(
            @Option(
                            longNames = "page",
                            shortNames = 'p',
                            description = "Номер страницы (начиная с 0)",
                            defaultValue = "0")
                    Integer page) {
        return executeWithErrorHandling(() -> parcelService.readAllParcels(page));
    }
}
