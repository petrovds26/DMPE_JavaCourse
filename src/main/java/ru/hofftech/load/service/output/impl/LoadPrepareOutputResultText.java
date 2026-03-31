package ru.hofftech.load.service.output.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.load.model.core.LoadStrategyParcelInvalid;
import ru.hofftech.load.model.enums.LoadStrategyParcelInvalidCauseType;
import ru.hofftech.load.service.output.LoadPrepareOutputResult;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.PlacedParcel;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.util.PrintStringUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация подготовки вывода результатов в текстовом формате.
 * Формирует детальный отчёт со статистикой, схемами машин и информацией о проблемных посылках.
 */
@Slf4j
@NullMarked
public class LoadPrepareOutputResultText implements LoadPrepareOutputResult {

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult output(LoadResult result) {
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

        return ProcessorCommandResult.createSuccess(output.toString());
    }

    /**
     * Выводит статистику обработки посылок.
     *
     * @param output StringBuilder для накопления вывода (не может быть null)
     * @param result результат обработки посылок (не может быть null)
     */
    private void printStatistics(StringBuilder output, LoadResult result) {
        List<LoadStrategyParcelInvalid> importParcelInvalids = result.loadStrategyParcelInvalids();
        List<Parcel> inputParcels = result.inputParcels();
        List<Machine> machines = result.machines();

        output.append("\nСТАТИСТИКА:\n");

        Long invalidCount = Optional.ofNullable(importParcelInvalids)
                .map(invalids -> invalids.stream()
                        .filter(p -> p.causeType() == LoadStrategyParcelInvalidCauseType.PARCEL_INVALID)
                        .count())
                .orElse(0L);
        Long oversizedCount = Optional.ofNullable(importParcelInvalids)
                .map(invalids -> invalids.stream()
                        .filter(p -> p.causeType() == LoadStrategyParcelInvalidCauseType.PARCEL_OVERSIZED)
                        .count())
                .orElse(0L);
        Long noMachineCount = Optional.ofNullable(importParcelInvalids)
                .map(invalids -> invalids.stream()
                        .filter(p -> p.causeType() == LoadStrategyParcelInvalidCauseType.NO_MACHINE_SPACE)
                        .count())
                .orElse(0L);
        int inputParcelsCount =
                Optional.ofNullable(inputParcels).map(List::size).orElse(0);
        int inputMachinesCount = Optional.ofNullable(machines).map(List::size).orElse(0);

        output.append(String.format("Всего обработано посылок: %d. Из них: %n", inputParcelsCount));
        output.append(String.format("Не прошли валидацию: %d%n", invalidCount));
        output.append(String.format("Не поместились в машину: %d%n", oversizedCount));
        output.append(String.format("Не хватило машин: %d%n", noMachineCount));
        output.append(String.format("Успешно упаковано: %d%n", result.getTotalParcelsProcessed()));
        output.append(String.format("Использовано машин: %d%n", inputMachinesCount));
    }

    /**
     * Выводит схемы всех машин с размещёнными посылками.
     *
     * @param output StringBuilder для накопления вывода (не может быть null)
     * @param result результат обработки посылок (не может быть null)
     */
    private void printMachines(StringBuilder output, LoadResult result) {
        List<Machine> machines = result.machines();

        if (machines == null || machines.isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("СХЕМЫ МАШИН:\n");

        for (int i = 0; i < machines.size(); i++) {
            Machine machine = machines.get(i);

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
     * @param output StringBuilder для накопления вывода (не может быть null)
     * @param result результат обработки посылок (не может быть null)
     */
    private void printErrors(StringBuilder output, LoadResult result) {
        List<String> errors = result.errors();
        if (errors == null || errors.isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("ОШИБКИ ПРИ ОБРАБОТКЕ:\n");

        // Группируем по причинам
        errors.forEach(error -> output.append(error).append("\n"));
    }

    /**
     * Выводит информацию о проблемных посылках, сгруппированную по причинам.
     *
     * @param output StringBuilder для накопления вывода (не может быть null)
     * @param result результат обработки посылок (не может быть null)
     */
    private void printInvalidParcels(StringBuilder output, LoadResult result) {
        List<LoadStrategyParcelInvalid> importParcelInvalids = result.loadStrategyParcelInvalids();
        if (importParcelInvalids == null || importParcelInvalids.isEmpty()) {
            return;
        }

        output.append("\n").append("=".repeat(40)).append("\n");
        output.append("ПРОБЛЕМНЫЕ ПОСЫЛКИ:\n");

        // Группируем по причинам
        importParcelInvalids.stream()
                .collect(Collectors.groupingBy(LoadStrategyParcelInvalid::causeType))
                .forEach((cause, parcels) -> {
                    output.append("\n").append(cause.getDescription()).append(":\n");
                    parcels.forEach(invalid -> {
                        Parcel parcel = invalid.parcel();
                        output.append(invalid.cause()).append("\n");
                        output.append(PrintStringUtil.parcelRender(parcel));
                        output.append("\n");
                    });
                });
    }

    /**
     * Формирует строковое представление машины с рамкой.
     * Строки идут сверху вниз для правильного отображения.
     *
     * @param machine машина для отображения (не может быть null)
     * @return строковое представление машины (не может быть null)
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
     * @param machine машина с посылками (не может быть null)
     * @return строковое представление информации о посылках (не может быть null)
     */
    private String getParcelsInfo(Machine machine) {
        StringBuilder sb = new StringBuilder();
        List<PlacedParcel> parcels = machine.parcels();

        sb.append("Размещено посылок: ").append(parcels.size()).append("\n");

        for (PlacedParcel p : parcels) {
            sb.append(PrintStringUtil.parcelRender(p.parcel(), p));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Форматирует информацию о посылке по единому шаблону.
     *
     * @param parcel посылка (не может быть null)
     * @param placed информация о размещении (может быть null)
     * @return отформатированная строка (не может быть null)
     */
    private String formatParcelInfo(Parcel parcel, @Nullable PlacedParcel placed) {
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
}
