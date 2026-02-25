package ru.hofftech.service.output.impl;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.model.core.Machine;
import ru.hofftech.model.core.Parcel;
import ru.hofftech.model.core.PlacedParcel;
import ru.hofftech.model.dto.LoadingResult;
import ru.hofftech.service.output.ParcelOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ParcelOutputLog implements ParcelOutput {

    @Override
    public void print(LoadingResult result) {
        StringBuilder output = new StringBuilder();

        output.append("\n").append("=".repeat(60)).append("\n");
        output.append("ОТЧЁТ ПО УПАКОВКЕ ПОСЫЛОК").append("\n");
        output.append("=".repeat(60)).append("\n");

        // Общая статистика
        printStatistics(output, result);

        // Детали по машинам
        printMachines(output, result);

        // Проблемные посылки
        printInvalidParcels(output, result);
        printOversizedParcels(output, result);

        output.append("=".repeat(60));

        log.info(output.toString());
    }

    private void printStatistics(StringBuilder output, LoadingResult result) {
        output.append("\nСТАТИСТИКА:\n");
        output.append(String.format(
                "Всего обработано посылок: %d. Из них: %n",
                result.inputParcels().size()));
        output.append(String.format(
                "Не прошли валидацию: %d%n", result.invalidParcels().size()));
        output.append(String.format(
                "Не поместились в машину: %d%n", result.oversizedParcels().size()));
        output.append(String.format("Успешно упаковано: %d%n", result.getTotalParcelsProcessed()));
        output.append(
                String.format("Использовано машин: %d%n", result.machines().size()));
    }

    private void printMachines(StringBuilder output, LoadingResult result) {
        if (result.machines().isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("СХЕМЫ МАШИН:\n");

        for (int i = 0; i < result.machines().size(); i++) {
            Machine machine = result.machines().get(i);

            output.append("\n").append("-".repeat(30)).append("\n");
            output.append("Машина #").append(i + 1).append("\n");
            output.append("-".repeat(30)).append("\n");

            // Схема машины
            output.append(renderMachine(machine)).append("\n");

            // Детальная информация о посылках в машине
            output.append("\n").append(getParcelsInfo(machine));
        }
    }

    private void printInvalidParcels(StringBuilder output, LoadingResult result) {
        if (result.invalidParcels().isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("ПОСЫЛКИ С ОШИБКАМИ ВАЛИДАЦИИ:\n");

        for (Parcel parcel : result.invalidParcels()) {
            output.append(formatParcelInfo(parcel));
            output.append("\n");
        }
    }

    private void printOversizedParcels(StringBuilder output, LoadingResult result) {
        if (result.oversizedParcels().isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("ПОСЫЛКИ, НЕ ПОМЕСТИВШИЕСЯ В МАШИНУ:\n");

        for (Parcel parcel : result.oversizedParcels()) {
            output.append(formatParcelInfo(parcel));
            output.append("\n");
        }
    }

    private String renderMachine(Machine machine) {
        StringBuilder sb = new StringBuilder();
        int width = machine.width();
        int height = machine.height();
        char[][] grid = machine.grid();

        // Верхняя граница
        sb.append("+".repeat(width + 2)).append("\n");

        // Содержимое (идём снизу вверх для правильного отображения)
        for (int i = height - 1; i >= 0; i--) {
            sb.append('+');
            for (int j = 0; j < width; j++) {
                sb.append(grid[i][j]);
            }
            sb.append('+');
            sb.append('\n');
        }

        // Нижняя граница
        sb.append("+".repeat(width + 2));

        return sb.toString();
    }

    /**
     * Детальная информация о размещённых посылках в машине
     */
    private String getParcelsInfo(Machine machine) {
        StringBuilder sb = new StringBuilder();
        List<PlacedParcel> parcels = machine.parcels();

        sb.append("Размещено посылок: ").append(parcels.size()).append("\n");

        for (PlacedParcel p : parcels) {
            sb.append(formatParcelInfo(p.parcel(), p));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Возвращает строковое представление посылки
     */
    private String renderParcel(Parcel parcel) {
        List<String> lines = new ArrayList<>(parcel.getLines());
        Collections.reverse(lines);
        return String.join("\n", lines);
    }

    /**
     * Форматирует информацию о посылке по единому шаблону
     * @param parcel посылка
     * @param placed информация о размещении (может быть null)
     * @return отформатированная строка
     */
    private String formatParcelInfo(Parcel parcel, PlacedParcel placed) {
        StringBuilder sb = new StringBuilder();

        // Заголовок с символом и габаритами
        sb.append(String.format("Посылка '%c' [%dx%d]", parcel.symbol(), parcel.getWidth(), parcel.getHeight()));

        // Позиция, если известна
        if (placed != null) {
            sb.append(String.format(
                    " позиция (%d,%d)-(%d,%d)", placed.x(), placed.y(), placed.getMaxX(), placed.getMaxY()));
        }
        sb.append("\n");

        // Рендер посылки
        sb.append(renderParcel(parcel)).append("\n");

        // Разделитель
        sb.append("-".repeat(30)).append("\n");

        return sb.toString();
    }

    private String formatParcelInfo(Parcel parcel) {
        return formatParcelInfo(parcel, null);
    }
}
