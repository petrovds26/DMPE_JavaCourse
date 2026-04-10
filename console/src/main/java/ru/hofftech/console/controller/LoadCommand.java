package ru.hofftech.console.controller;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;
import ru.hofftech.console.service.LoadService;

/**
 * Консольная команда для загрузки посылок в машины.
 */
@NullMarked
@RequiredArgsConstructor
@Component
@Command(command = "load", description = "Метод загрузки посылок в машины")
public class LoadCommand extends BaseCommand {
    private final LoadService loadService;

    /**
     * Выполняет загрузку посылок в машины.
     *
     * @param inputParcelFile путь к файлу с посылками (опционально)
     * @param inputParcelText текст с посылками (опционально)
     * @param outputFile      путь к выходному файлу (опционально)
     * @param outputType      тип вывода результата
     * @param strategyId      идентификатор стратегии упаковки
     * @param machines        описание доступных машин
     * @param userId          идентификатор пользователя
     * @return результат выполнения операции
     */
    @Command(command = "", description = "Загрузка посылок")
    public String load(
            @Option(longNames = "parcelsFile", description = "Входной файл (поддерживаются .txt и .json)") @Nullable
                    String inputParcelFile,
            @Option(longNames = "parcelsText", description = "Посылки текстом") @Nullable String inputParcelText,
            @Option(longNames = "outputFile", description = "Выходной файл для сохранения результата") @Nullable
                    String outputFile,
            @Option(longNames = "output", description = "Тип вывода результата", required = true) String outputType,
            @Option(longNames = "strategy", description = "Стратегия упаковки", required = true) String strategyId,
            @Option(longNames = "machines", description = "Формы доступных машин", required = true) String machines,
            @Option(longNames = "userId", description = "Пользователь", required = true) String userId) {

        String parcelFile = inputParcelFile == null ? "" : inputParcelFile;
        String parcelText = inputParcelText == null ? "" : inputParcelText;
        String output = outputFile == null ? "" : outputFile;

        return executeWithErrorHandling(
                () -> loadService.loadParcel(parcelFile, parcelText, output, outputType, strategyId, machines, userId));
    }
}
