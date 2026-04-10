package ru.hofftech.console.controller;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;
import ru.hofftech.console.service.UnloadService;

/**
 * Консольная команда для разгрузки машин.
 */
@NullMarked
@RequiredArgsConstructor
@Component
@Command(command = "unload", description = "Метод разгрузки машины")
public class UnloadCommand extends BaseCommand {
    private final UnloadService unloadService;

    /**
     * Выполняет разгрузку машин из файла.
     *
     * @param inputFile  путь к входному JSON файлу
     * @param outputFile путь к выходному файлу (опционально)
     * @param outputType тип вывода результата
     * @param userId     идентификатор пользователя
     * @return результат выполнения операции
     */
    @Command(command = "", description = "Разгрузка машины")
    public String unload(
            @Option(longNames = "inputFile", description = "Входной файл (поддерживаются .json)", required = true)
                    String inputFile,
            @Option(longNames = "outputFile", description = "Выходной файл для сохранения результата") @Nullable
                    String outputFile,
            @Option(longNames = "output", description = "Тип вывода результата", required = true) String outputType,
            @Option(longNames = "userId", description = "Пользователь", required = true) String userId) {
        String output = outputFile == null ? "" : outputFile;

        return executeWithErrorHandling(() -> unloadService.unloadMachines(inputFile, output, outputType, userId));
    }
}
