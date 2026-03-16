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
import ru.hofftech.load.model.enums.LoadInputParcelType;
import ru.hofftech.load.model.enums.LoadOutputType;
import ru.hofftech.load.service.command.impl.LoadParcelConsoleCommand;
import ru.hofftech.load.service.command.impl.LoadParcelTelegramCommand;
import ru.hofftech.load.service.loader.strategy.LoadStrategyService;
import ru.hofftech.load.service.loader.strategy.impl.LoadStrategyBalancedPacking;
import ru.hofftech.load.service.loader.strategy.impl.LoadStrategyDensePacking;
import ru.hofftech.load.service.loader.strategy.impl.LoadStrategyOneParcelPerMachine;
import ru.hofftech.load.service.output.LoadOutputPrepareService;
import ru.hofftech.load.service.output.impl.LoadPrepareOutputResultJson;
import ru.hofftech.load.service.output.impl.LoadPrepareOutputResultText;
import ru.hofftech.load.service.parser.LoadParcelParserService;
import ru.hofftech.readparcel.service.command.impl.ReadAllParcelTelegramCommand;
import ru.hofftech.readparcel.service.command.impl.ReadParcelConsoleCommand;
import ru.hofftech.readparcel.service.command.impl.ReadParcelTelegramCommand;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.FileSaveService;
import ru.hofftech.shared.service.command.console.ConsoleCommand;
import ru.hofftech.shared.service.command.console.impl.EmptyConsoleCommand;
import ru.hofftech.shared.service.command.console.impl.ExitConsoleCommand;
import ru.hofftech.shared.service.command.telegram.TelegramCommand;
import ru.hofftech.shared.service.command.telegram.impl.CancelTelegramCommand;
import ru.hofftech.shared.service.parser.ParserParams;
import ru.hofftech.shared.service.parser.ParserParcelBuilder;
import ru.hofftech.shared.service.parser.ParserParcelNormalizer;
import ru.hofftech.shared.service.parser.impl.ParserMachineFromFormString;
import ru.hofftech.shared.service.parser.impl.ParserParcelFromFormDto;
import ru.hofftech.shared.service.parser.impl.ParserParcelFromNameJsonFile;
import ru.hofftech.shared.service.parser.impl.ParserParcelFromNameString;
import ru.hofftech.shared.service.parser.impl.ParserParcelFromNameTxtFile;
import ru.hofftech.shared.service.telegram.TelegramUserSessionService;
import ru.hofftech.shared.validation.impl.InputFilePathValidator;
import ru.hofftech.shared.validation.impl.OutputFilePathValidator;
import ru.hofftech.shared.validation.impl.ParcelGridValidator;
import ru.hofftech.shared.validation.impl.ParcelListStringValidator;
import ru.hofftech.updateparcel.service.command.impl.UpdateParcelConsoleCommand;
import ru.hofftech.updateparcel.service.command.impl.UpdateParcelTelegramCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        InputFilePathValidator inputFilePathValidator = new InputFilePathValidator();
        OutputFilePathValidator outputFilePathValidator = new OutputFilePathValidator();
        ParcelGridValidator gridValidator = new ParcelGridValidator();
        ParcelListStringValidator stringValidator = new ParcelListStringValidator();

        // Парсеры
        ParserParams parserParams = new ParserParams();
        ParserParcelNormalizer normalizer = new ParserParcelNormalizer();
        ParserParcelBuilder parserParcelBuilder = new ParserParcelBuilder();
        ParserParcelFromFormDto parserParcelFromFormDto =
                new ParserParcelFromFormDto(stringValidator, normalizer, parserParcelBuilder, gridValidator);
        ParserParcelFromNameString parserParcelFromNameString = new ParserParcelFromNameString(parcelRepository);
        ParserParcelFromNameTxtFile parserParcelFromNameTxtFile = new ParserParcelFromNameTxtFile(parcelRepository);
        ParserParcelFromNameJsonFile parserParcelFromNameJsonFile = new ParserParcelFromNameJsonFile(parcelRepository);
        ParserMachineFromFormString parserMachineFromFormString = new ParserMachineFromFormString();

        // Сохранение в файл
        FileSaveService fileSaveService = new FileSaveService();

        // Подготовка выводов результатов
        LoadPrepareOutputResultText loadPrepareOutputResultText = new LoadPrepareOutputResultText();
        LoadPrepareOutputResultJson loadPrepareOutputResultJson = new LoadPrepareOutputResultJson();
        // Сервисы для фич
        LoadStrategyService loadStrategyService = new LoadStrategyService(List.of(
                new LoadStrategyOneParcelPerMachine(),
                new LoadStrategyDensePacking(),
                new LoadStrategyBalancedPacking()));
        LoadParcelParserService loadParcelParserService = new LoadParcelParserService(Map.of(
                LoadInputParcelType.TEXT, parserParcelFromNameString,
                LoadInputParcelType.TEXT_FILE, parserParcelFromNameTxtFile,
                LoadInputParcelType.JSON_FILE, parserParcelFromNameJsonFile));

        LoadOutputPrepareService loadOutputPrepareService = new LoadOutputPrepareService(Map.of(
                LoadOutputType.TEXT, loadPrepareOutputResultText,
                LoadOutputType.TEXT_FILE, loadPrepareOutputResultText,
                LoadOutputType.JSON_FILE, loadPrepareOutputResultJson));

        // Консольные команды

        EmptyConsoleCommand emptyConsoleCommand = new EmptyConsoleCommand();
        CreateParcelConsoleCommand createParcelConsoleCommand =
                new CreateParcelConsoleCommand(parserParams, parserParcelFromFormDto, parcelRepository);
        ReadParcelConsoleCommand readParcelConsoleCommand =
                new ReadParcelConsoleCommand(parserParams, parcelRepository);
        UpdateParcelConsoleCommand updateParcelConsoleCommand =
                new UpdateParcelConsoleCommand(parserParams, parserParcelFromFormDto, parcelRepository);
        DeleteParcelConsoleCommand deleteParcelConsoleCommand =
                new DeleteParcelConsoleCommand(parserParams, parcelRepository);
        LoadParcelConsoleCommand loadParcelConsoleCommand = new LoadParcelConsoleCommand(
                parserParams,
                inputFilePathValidator,
                outputFilePathValidator,
                loadStrategyService,
                loadParcelParserService,
                parserMachineFromFormString,
                loadOutputPrepareService,
                fileSaveService);
        ExitConsoleCommand exitConsoleCommand = new ExitConsoleCommand();

        // Телеграм команды
        CreateParcelTelegramCommand createParcelTelegramCommand =
                new CreateParcelTelegramCommand(parcelRepository, parserParcelFromFormDto);
        ReadParcelTelegramCommand readParcelTelegramCommand = new ReadParcelTelegramCommand(parcelRepository);
        ReadAllParcelTelegramCommand readAllParcelTelegramCommand = new ReadAllParcelTelegramCommand(parcelRepository);
        UpdateParcelTelegramCommand updateParcelTelegramCommand =
                new UpdateParcelTelegramCommand(parcelRepository, parserParcelFromFormDto);
        DeleteParcelTelegramCommand deleteParcelTelegramCommand = new DeleteParcelTelegramCommand(parcelRepository);
        LoadParcelTelegramCommand loadParcelTelegramCommand = new LoadParcelTelegramCommand(
                parserParcelFromNameString,
                parserMachineFromFormString,
                loadStrategyService,
                loadPrepareOutputResultText);
        CancelTelegramCommand cancelTelegramCommand = new CancelTelegramCommand();

        // Сбор листов с командами:
        List<ConsoleCommand> consoleCommands = new ArrayList<>(List.of(
                emptyConsoleCommand,
                createParcelConsoleCommand,
                readParcelConsoleCommand,
                updateParcelConsoleCommand,
                deleteParcelConsoleCommand,
                loadParcelConsoleCommand,
                exitConsoleCommand));

        List<TelegramCommand> telegramCommands = new ArrayList<>(List.of(
                createParcelTelegramCommand,
                readParcelTelegramCommand,
                readAllParcelTelegramCommand,
                updateParcelTelegramCommand,
                deleteParcelTelegramCommand,
                loadParcelTelegramCommand,
                cancelTelegramCommand));

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

        prepareData(createParcelConsoleCommand);
    }

    private static void prepareData(CreateParcelConsoleCommand cr) {
        // Стандартные посылки
        cr.execute("create_parcel --name \"Посылка Тип 1\" --form \"1\" --symbol \"1\"");
        cr.execute("create_parcel --name \"Посылка Тип 2\" --form \"22\" --symbol \"2\"");
        cr.execute("create_parcel --name \"Посылка Тип 3\" --form \"333\" --symbol \"3\"");
        cr.execute("create_parcel --name \"Посылка Тип 4\" --form \"4444\" --symbol \"4\"");
        cr.execute("create_parcel --name \"Посылка Тип 5\" --form \"55555\" --symbol \"5\"");
        cr.execute("create_parcel --name \"Посылка Тип 6\" --form \"666\\n666\" --symbol \"6\"");
        cr.execute("create_parcel --name \"Посылка Тип 7\" --form \"777\\n7777\" --symbol \"7\"");
        cr.execute("create_parcel --name \"Посылка Тип 8\" --form \"8888\\n8888\" --symbol \"8\"");
        cr.execute("create_parcel --name \"Посылка Тип 9\" --form \"999\\n999\\n999\" --symbol \"9\"");

        // Штанга
        cr.execute("create_parcel --name \"Штанга\" --form \"#      #\\n########\\n#      #\" --symbol \"#\"");

        // Крест
        cr.execute("create_parcel --name \"Крест\" --form \"  #  \\n ### \\n#####\\n ### \\n  #  \" --symbol \"#\"");

        // Змейка
        cr.execute("create_parcel --name \"Змейка\" --form \"###\\n  #\\n###\\n#  \\n###\" --symbol \"#\"");

        // Пирамида
        cr.execute("create_parcel --name \"Пирамида\" --form \"  #  \\n ### \\n#####\" --symbol \"#\"");

        // Ромб
        cr.execute("create_parcel --name \"Ромб\" --form \"  #  \\n ### \\n#####\\n ### \\n  #  \" --symbol \"#\"");

        // Лабиринт
        cr.execute("create_parcel --name \"Лабиринт\" --form \"#####\\n#   #\\n#   #\\n#   #\\n#####\" --symbol \"#\"");
    }
}
