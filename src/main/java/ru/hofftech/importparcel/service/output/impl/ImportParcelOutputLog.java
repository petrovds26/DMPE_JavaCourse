package ru.hofftech.importparcel.service.output.impl;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.importparcel.model.core.ImportParcelInvalid;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.model.enums.ImportParcelInvalidCauseType;
import ru.hofftech.importparcel.service.output.ImportParcelOutput;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.PlacedParcel;
import ru.hofftech.shared.model.enums.FileType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация вывода результатов упаковки посылок в лог.
 * Формирует детальный отчёт со статистикой, схемами машин
 * и информацией о проблемных посылках.
 */
@Slf4j
public class ImportParcelOutputLog implements ImportParcelOutput {

    @Override
    public FileType getFileType() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Вывод в лог";
    }

    @Override
    public void output(ImportParcelResult result, String filePath) {
        StringBuilder output = new StringBuilder();
        // Ошибки обработки
        printErrors(output, result);

        output.append("\n").append("=".repeat(60)).append("\n");
        output.append("ОТЧЁТ ПО УПАКОВКЕ ПОСЫЛОК").append("\n");
        output.append("=".repeat(60)).append("\n");

        // Общая статистика
        printStatistics(output, result);

        // Детали по машинам
        printMachines(output, result);

        // Проблемные посылки
        printInvalidParcels(output, result);

        output.append("=".repeat(60));

        log.info(output.toString());
    }

    /**
     * Выводит статистику обработки посылок.
     *
     * @param output StringBuilder для накопления вывода
     * @param result результат обработки посылок
     */
    private void printStatistics(StringBuilder output, ImportParcelResult result) {
        output.append("\nСТАТИСТИКА:\n");

        Long invalidCount = result.importParcelInvalids().stream()
                .filter(p -> p.causeType() == ImportParcelInvalidCauseType.PARCEL_INVALID)
                .count();
        Long oversizedCount = result.importParcelInvalids().stream()
                .filter(p -> p.causeType() == ImportParcelInvalidCauseType.PARCEL_OVERSIZED)
                .count();
        Long noMachineCount = result.importParcelInvalids().stream()
                .filter(p -> p.causeType() == ImportParcelInvalidCauseType.NO_MACHINE_SPACE)
                .count();

        output.append(String.format(
                "Всего обработано посылок: %d. Из них: %n",
                result.inputParcels().size()));
        output.append(String.format("Не прошли валидацию: %d%n", invalidCount));
        output.append(String.format("Не поместились в машину: %d%n", oversizedCount));
        output.append(String.format("Не хватило машин: %d%n", noMachineCount));
        output.append(String.format("Успешно упаковано: %d%n", result.getTotalParcelsProcessed()));
        output.append(
                String.format("Использовано машин: %d%n", result.machines().size()));
    }

    /**
     * Выводит схемы всех машин с размещёнными посылками.
     *
     * @param output StringBuilder для накопления вывода
     * @param result результат обработки посылок
     */
    private void printMachines(StringBuilder output, ImportParcelResult result) {
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

    /**
     * Выводит общие ошибки обработки, не привязанные к конкретным посылкам.
     *
     * @param output StringBuilder для накопления вывода
     * @param result результат обработки посылок
     */
    private void printErrors(StringBuilder output, ImportParcelResult result) {
        if (result.errors().isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("ОШИБКИ ПРИ ОБРАБОТКЕ:\n");

        // Группируем по причинам
        result.errors().forEach(error -> output.append(error).append("\n"));
    }

    /**
     * Выводит информацию о проблемных посылках, сгруппированную по причинам.
     *
     * @param output StringBuilder для накопления вывода
     * @param result результат обработки посылок
     */
    private void printInvalidParcels(StringBuilder output, ImportParcelResult result) {
        if (result.importParcelInvalids().isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("ПРОБЛЕМНЫЕ ПОСЫЛКИ:\n");

        // Группируем по причинам
        result.importParcelInvalids().stream()
                .collect(Collectors.groupingBy(ImportParcelInvalid::causeType))
                .forEach((cause, parcels) -> {
                    output.append("\n").append(cause.getDescription()).append(":\n");
                    parcels.forEach(invalid -> {
                        Parcel parcel = invalid.parcel();
                        output.append(invalid.cause()).append("\n");
                        output.append(formatParcelInfo(parcel));
                        output.append("\n");
                    });
                });
    }

    /**
     * Формирует строковое представление машины с рамкой.
     * Строки идут сверху вниз для правильного отображения.
     *
     * @param machine машина для отображения
     * @return строковое представление машины
     */
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

    /**
     * Возвращает детальную информацию о размещённых посылках в машине.
     *
     * @param machine машина с посылками
     * @return строковое представление информации о посылках
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
        sb.append(parcel.getForm()).append("\n");

        // Разделитель
        sb.append("-".repeat(30)).append("\n");

        return sb.toString();
    }

    /**
     * Перегрузка метода formatParcelInfo для случаев без координат.
     *
     * @param parcel посылка
     * @return отформатированная строка
     */
    private String formatParcelInfo(Parcel parcel) {
        return formatParcelInfo(parcel, null);
    }
}
