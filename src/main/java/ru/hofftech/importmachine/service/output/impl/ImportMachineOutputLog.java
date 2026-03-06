package ru.hofftech.importmachine.service.output.impl;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.importmachine.model.core.ImportMachineResult;
import ru.hofftech.importmachine.service.output.ImportMachineOutput;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.enums.FileType;

import java.util.List;
import java.util.Optional;

@Slf4j
public class ImportMachineOutputLog implements ImportMachineOutput {

    @Override
    public Optional<FileType> getFileTypeOptional() {
        return Optional.empty();
    }

    @Override
    public String getDescription() {
        return "Вывод в лог";
    }

    @Override
    public void output(ImportMachineResult result, String filePath) {
        StringBuilder output = new StringBuilder();

        output.append("\n").append("=".repeat(60)).append("\n");
        output.append("ОТЧЁТ ПО ЗАГРУЗКЕ МАШИН").append("\n");
        output.append("=".repeat(60)).append("\n");

        // Общая статистика
        printStatistics(output, result);

        // Детали по машинам
        printMachines(output, result);

        // Загруженные посылки
        printLoadedParcels(output, result);

        output.append("=".repeat(60));

        log.info(output.toString());
    }

    private void printStatistics(StringBuilder output, ImportMachineResult result) {
        output.append("\nСТАТИСТИКА:\n");
        output.append(String.format(
                "Всего машин на входе: %d%n", result.inputMachines().size()));
        output.append(String.format(
                "Успешно загружено посылок: %d%n", result.parcels().size()));
    }

    private void printMachines(StringBuilder output, ImportMachineResult result) {
        if (result.inputMachines().isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("СХЕМЫ МАШИН ДО ЗАГРУЗКИ:\n");

        for (int i = 0; i < result.inputMachines().size(); i++) {
            Machine machine = result.inputMachines().get(i);

            output.append("\n").append("-".repeat(30)).append("\n");
            output.append("Машина #").append(i + 1).append("\n");
            output.append("-".repeat(30)).append("\n");

            // Схема машины
            output.append(renderMachine(machine)).append("\n");

            // Информация о свободном месте в машине
            output.append(getMachineInfo(machine));
        }
    }

    private void printLoadedParcels(StringBuilder output, ImportMachineResult result) {
        if (result.parcels().isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("ЗАГРУЖЕННЫЕ ПОСЫЛКИ:\n");

        for (int i = 0; i < result.parcels().size(); i++) {
            Parcel parcel = result.parcels().get(i);
            output.append("\n").append("-".repeat(30)).append("\n");
            output.append("Посылка #").append(i + 1).append("\n");
            output.append("-".repeat(30)).append("\n");
            output.append(formatParcelInfo(parcel));
        }
    }

    private String renderMachine(Machine machine) {
        StringBuilder sb = new StringBuilder();
        List<String> lines = machine.getLines();
        int width = machine.width();

        // Верхняя граница
        sb.append("+".repeat(width + 2)).append("\n");

        // Содержимое
        for (String line : lines) {
            sb.append('+').append(line).append('+').append('\n');
        }

        // Нижняя граница
        sb.append("+".repeat(width + 2));

        return sb.toString();
    }

    private String getMachineInfo(Machine machine) {
        StringBuilder sb = new StringBuilder();

        // Считаем свободное место
        int freeSpace = 0;
        char[][] grid = machine.grid();
        for (int i = 0; i < machine.height(); i++) {
            for (int j = 0; j < machine.width(); j++) {
                if (grid[i][j] == ' ') {
                    freeSpace++;
                }
            }
        }

        sb.append("\nСвободное место: ").append(freeSpace).append(" клеток\n");
        sb.append("Размеры: ")
                .append(machine.width())
                .append("x")
                .append(machine.height())
                .append("\n");

        return sb.toString();
    }

    /**
     * Форматирует информацию о посылке
     */
    private String formatParcelInfo(Parcel parcel) {

        // Заголовок с символом и габаритами

        return String.format("Посылка '%c' [%dx%d]", parcel.symbol(), parcel.getWidth(), parcel.getHeight()) + "\n"
                + parcel.getForm() + "\n";
    }
}
