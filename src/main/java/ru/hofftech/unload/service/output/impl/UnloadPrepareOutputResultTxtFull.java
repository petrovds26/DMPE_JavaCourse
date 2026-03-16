package ru.hofftech.unload.service.output.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.unload.model.core.UnloadResult;
import ru.hofftech.unload.service.output.UnloadPrepareOutputResult;

import java.util.List;

@Slf4j
public class UnloadPrepareOutputResultTxtFull implements UnloadPrepareOutputResult {

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult output(@NonNull UnloadResult result) {
        StringBuilder output = new StringBuilder();

        output.append("\n").append("=".repeat(60)).append("\n");
        output.append("ОТЧЁТ ПО РАЗГРУЗКЕ МАШИН").append("\n");
        output.append("=".repeat(60)).append("\n");

        // Общая статистика
        printStatistics(output, result);

        // Детали по машинам
        printMachines(output, result);

        // Загруженные посылки
        printLoadedParcels(output, result);

        output.append("=".repeat(60));

        return ProcessorCommandResult.builder()
                .success(true)
                .message(output.toString())
                .build();
    }

    private void printStatistics(@NonNull StringBuilder output, @NonNull UnloadResult result) {
        output.append("\nСТАТИСТИКА:\n");
        output.append(String.format(
                "Всего машин на входе: %d%n", result.inputMachines().size()));
        output.append(String.format(
                "Успешно разгружено посылок: %d%n", result.parcels().size()));
    }

    private void printMachines(@NonNull StringBuilder output, @NonNull UnloadResult result) {
        if (result.inputMachines().isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("СХЕМЫ МАШИН ДО РАЗГРУЗКИ:\n");

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

    private void printLoadedParcels(@NonNull StringBuilder output, @NonNull UnloadResult result) {
        if (result.parcels().isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("РАЗГРУЖЕННЫЕ ПОСЫЛКИ:\n");

        for (int i = 0; i < result.parcels().size(); i++) {
            Parcel parcel = result.parcels().get(i);
            output.append("\n").append("-".repeat(30)).append("\n");
            output.append("Посылка #").append(i + 1).append("\n");
            output.append("-".repeat(30)).append("\n");
            output.append(formatParcelInfo(parcel));
        }
    }

    private @NonNull String renderMachine(@NonNull Machine machine) {
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

    private @NonNull String getMachineInfo(@NonNull Machine machine) {
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
     * Перегрузка метода formatParcelInfo для случаев без координат.
     *
     * @param parcel посылка
     * @return отформатированная строка (не может быть null)
     */
    private @NonNull String formatParcelInfo(@NonNull Parcel parcel) {

        // Заголовок с символом и габаритами

        return String.format("Посылка '%c' [%dx%d]", parcel.symbol(), parcel.getWidth(), parcel.getHeight()) + "\n"
                + parcel.getForm() + "\n";
    }
}
