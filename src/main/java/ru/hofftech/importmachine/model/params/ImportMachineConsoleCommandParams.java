package ru.hofftech.importmachine.model.params;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Параметры для команды импорта посылок
 */
@Getter
@ToString
@Parameters(commandDescription = "Импорт машин из файла")
@RequiredArgsConstructor
public class ImportMachineConsoleCommandParams {

    @Parameter(
            names = {"--input", "-i"},
            description = "Входной файл (поддерживается .json)",
            required = true,
            order = 0)
    private String inputFile;

    @Parameter(
            names = {"--output", "-o"},
            description = "Выходной JSON файл для результата",
            order = 1)
    private String outputFile;

    @Parameter(
            names = {"--help", "-h"},
            description = "Показать справку",
            help = true,
            order = 4)
    private boolean help;
}
