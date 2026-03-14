package ru.hofftech;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.hofftech.controller.ConsoleController;
import ru.hofftech.controller.TelegramController;
import ru.hofftech.createparcel.service.command.impl.CreateParcelConsoleCommand;
import ru.hofftech.createparcel.service.command.impl.CreateParcelTelegramCommand;
import ru.hofftech.deleteparcel.service.command.impl.DeleteParcelConsoleCommand;
import ru.hofftech.deleteparcel.service.command.impl.DeleteParcelTelegramCommand;
import ru.hofftech.readparcel.service.command.impl.ReadAllParcelTelegramCommand;
import ru.hofftech.readparcel.service.command.impl.ReadParcelConsoleCommand;
import ru.hofftech.readparcel.service.command.impl.ReadParcelTelegramCommand;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.console.ConsoleCommand;
import ru.hofftech.shared.service.command.console.impl.EmptyConsoleCommand;
import ru.hofftech.shared.service.command.console.impl.ExitConsoleCommand;
import ru.hofftech.shared.service.command.telegram.TelegramCommand;
import ru.hofftech.shared.service.command.telegram.impl.CancelTelegramCommand;
import ru.hofftech.shared.service.parser.ParserParams;
import ru.hofftech.shared.service.parser.ParserParcelBuilder;
import ru.hofftech.shared.service.parser.ParserParcelNormalizer;
import ru.hofftech.shared.service.parser.ParserParcelProcessor;
import ru.hofftech.shared.service.telegram.TelegramUserSessionService;
import ru.hofftech.shared.validation.impl.ParcelGridValidator;
import ru.hofftech.shared.validation.impl.ParcelListStringValidator;
import ru.hofftech.updateparcel.service.command.impl.UpdateParcelConsoleCommand;
import ru.hofftech.updateparcel.service.command.impl.UpdateParcelTelegramCommand;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main {
    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(@NonNull String[] args) {
        log.info("Стартуем приложение...");
        Main.start();
    }

    /**
     * Инициализирует все зависимости и запускает интерфейсы пользователя.
     */
    private static void start() {
        // Репозитории
        ParcelRepository parcelRepository = new ParcelRepository();

        // Валидаторы
        ParcelGridValidator gridValidator = new ParcelGridValidator();
        ParcelListStringValidator stringValidator = new ParcelListStringValidator();

        // Парсеры
        ParserParams parserParams = new ParserParams();
        ParserParcelNormalizer normalizer = new ParserParcelNormalizer();
        ParserParcelBuilder parserParcelBuilder = new ParserParcelBuilder();
        ParserParcelProcessor parserParcelProcessor =
                new ParserParcelProcessor(stringValidator, normalizer, parserParcelBuilder, gridValidator);

        // Консольные команды
        List<ConsoleCommand> consoleCommands = new ArrayList<>(List.of(
                new EmptyConsoleCommand(),
                new CreateParcelConsoleCommand(parserParams, parserParcelProcessor, parcelRepository),
                new ReadParcelConsoleCommand(parserParams, parcelRepository),
                new UpdateParcelConsoleCommand(parserParams, parserParcelProcessor, parcelRepository),
                new DeleteParcelConsoleCommand(parserParams, parcelRepository),
                new ExitConsoleCommand()));

        // Телеграм команды
        List<TelegramCommand> telegramCommands = new ArrayList<>(List.of(
                new CreateParcelTelegramCommand(parcelRepository, parserParcelProcessor),
                new ReadParcelTelegramCommand(parcelRepository),
                new ReadAllParcelTelegramCommand(parcelRepository),
                new UpdateParcelTelegramCommand(parcelRepository, parserParcelProcessor),
                new DeleteParcelTelegramCommand(parcelRepository),
                new CancelTelegramCommand()));

        // Запускаем консольный интерфейс (в отдельном потоке)
        Thread consoleThread = new Thread(() -> new ConsoleController(consoleCommands).listen());
        consoleThread.start();

        // Запускаем Telegram бота
        TelegramUserSessionService sessionService = new TelegramUserSessionService();
        TelegramController telegramController = new TelegramController(telegramCommands, sessionService);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramController);
            log.info("Telegram бот успешно запущен");
        } catch (TelegramApiException e) {
            log.error("Ошибка при запуске Telegram бота", e);
        }
    }
}
