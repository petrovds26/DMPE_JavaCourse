package ru.hofftech.importparcel.model.params;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.validators.PositiveInteger;
import lombok.Getter;
import lombok.ToString;

/**
 * Параметры для команды импорта посылок
 */
@Getter
@ToString
@Parameters(commandDescription = "Импорт посылок из файла")
public class ImportParcelConsoleCommandParams {

    @Parameter(
            names = {"--input", "-i"},
            description = "Входной файл (поддерживаются .txt и .json)",
            required = true,
            order = 0)
    private String inputFile;

    @Parameter(
            names = {"--output", "-o"},
            description = "Выходной JSON файл для результата",
            order = 1)
    private String outputFile;

    @Parameter(
            names = {"--strategy", "-s"},
            description = "Стратегия упаковки",
            required = true,
            order = 2)
    private Integer strategyId;

    @Parameter(
            names = {"--trucks", "-t"},
            description = "Количество доступных машин",
            required = true,
            validateWith = PositiveInteger.class,
            order = 3)
    private Integer truckCount;

    @Parameter(
            names = {"--help", "-h"},
            description = "Показать справку",
            help = true,
            order = 4)
    private boolean help;
}
