package ru.hofftech.shared.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.LoadParcelInvalidDto;
import ru.hofftech.shared.model.dto.LoadResponseDto;
import ru.hofftech.shared.model.dto.LoadStatisticDto;
import ru.hofftech.shared.model.dto.MachineDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.PlacedParcelDto;
import ru.hofftech.shared.model.dto.UnloadResponseDto;
import ru.hofftech.shared.model.dto.UnloadStatisticDto;
import ru.hofftech.shared.model.enums.LoadStrategyParcelInvalidCauseType;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Утилита для форматированного вывода посылок и отчётов.
 * <p>
 * Предоставляет методы для преобразования объектов в удобочитаемый текстовый формат,
 * используемый в консольном выводе и Telegram боте.
 */
@NullMarked
@UtilityClass
public class PrintStringUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Форматирует информацию о посылке с учётом размещения.
     *
     * @param parcel  DTO посылки
     * @param placed  информация о размещении (может быть null)
     * @return отформатированная строка с информацией о посылке
     */
    public static String parcelRender(ParcelDto parcel, @Nullable PlacedParcelDto placed) {
        StringBuilder sb = new StringBuilder();

        // Заголовок с символом и габаритами
        sb.append(String.format("Посылка '%s' [%dx%d]", parcel.name(), parcel.width(), parcel.height()))
                .append("\n");
        sb.append(String.format("Символ: '%c'", parcel.symbol())).append("\n");

        // Позиция, если известна
        if (placed != null) {
            sb.append(String.format(
                            "Позиция (%d,%d)-(%d,%d)",
                            placed.x(),
                            placed.y(),
                            calcParcelPlacedMaxX(parcel, placed),
                            calcParcelPlacedMaxY(parcel, placed)))
                    .append("\n");
        }

        // Рендер посылки
        sb.append(parcel.form()).append("\n");

        // Разделитель
        sb.append("-".repeat(30)).append("\n");

        return sb.toString();
    }

    /**
     * Форматирует информацию о посылке без координат размещения.
     *
     * @param parcel DTO посылки
     * @return отформатированная строка с информацией о посылке
     */
    public static String parcelRender(ParcelDto parcel) {
        return parcelRender(parcel, null);
    }

    /**
     * Форматирует отчёт о загрузке посылок.
     *
     * @param result результат загрузки
     * @return отформатированный отчёт
     */
    public static String renderLoadResponse(LoadResponseDto result) {
        StringBuilder sb = new StringBuilder();
        List<String> errors = result.statistic().errors();
        if (errors != null && !errors.isEmpty()) {
            sb.append(renderErrors(errors));
        }

        sb.append("\n").append("=".repeat(60)).append("\n");
        sb.append("ОТЧЁТ ПО УПАКОВКЕ ПОСЫЛОК").append("\n");
        sb.append("=".repeat(60)).append("\n");

        sb.append(renderLoadStatistic(result.statistic()));

        List<MachineDto> machines = result.machines();
        if (machines != null && !machines.isEmpty()) {
            sb.append(renderMachines(machines));
        }

        List<LoadParcelInvalidDto> invalidParcels = result.statistic().invalidParcels();
        if (invalidParcels != null && !invalidParcels.isEmpty()) {
            sb.append(renderInvalidParcels(invalidParcels));
        }

        sb.append("=".repeat(60));

        return sb.toString();
    }

    /**
     * Формирует строковое представление машины с рамкой.
     *
     * @param machine DTO машины
     * @return строковое представление машины
     */
    public static String renderMachineForm(MachineDto machine) {
        StringBuilder sb = new StringBuilder();
        List<String> lines = machine.form().lines().toList();
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
     * Выводит схемы всех машин с размещёнными посылками.
     *
     * @param machineList список машин
     * @return строковое представление всех машин
     */
    public static String renderMachines(List<MachineDto> machineList) {
        if (machineList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n").append("=".repeat(40)).append("\n");
        sb.append("СХЕМЫ МАШИН:\n");

        for (int i = 0; i < machineList.size(); i++) {
            MachineDto machine = machineList.get(i);

            sb.append("\n").append("-".repeat(30)).append("\n");
            sb.append("Машина #").append(i + 1).append("\n");
            sb.append("-".repeat(30)).append("\n");

            // Схема машины
            sb.append(renderMachineForm(machine)).append("\n");

            // Детальная информация о посылках в машине
            sb.append("\n").append(renderMachineParcelsInfo(machine));
        }

        return sb.toString();
    }

    /**
     * Возвращает детальную информацию о размещённых посылках в машине.
     *
     * @param machine DTO машины
     * @return строковое представление информации о посылках
     */
    public static String renderMachineParcelsInfo(MachineDto machine) {
        StringBuilder sb = new StringBuilder();
        List<PlacedParcelDto> parcels = machine.parcels();

        sb.append("Размещено посылок: ").append(parcels.size()).append("\n");

        for (PlacedParcelDto placedParcel : parcels) {
            sb.append(parcelRender(placedParcel.parcel(), placedParcel));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Выводит общие ошибки обработки.
     *
     * @param errors список ошибок
     * @return строковое представление ошибок
     */
    public static String renderErrors(List<String> errors) {
        if (errors.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n").append("=".repeat(40)).append("\n");
        sb.append("ОШИБКИ ПРИ ОБРАБОТКЕ:\n");

        // Группируем по причинам
        errors.forEach(error -> sb.append(error).append("\n"));

        return sb.toString();
    }

    /**
     * Выводит информацию о проблемных посылках, сгруппированную по причинам.
     *
     * @param invalidParcels список проблемных посылок
     * @return строковое представление проблемных посылок
     */
    public static String renderInvalidParcels(List<LoadParcelInvalidDto> invalidParcels) {
        if (invalidParcels.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        sb.append("\n").append("=".repeat(40)).append("\n");
        sb.append("ПРОБЛЕМНЫЕ ПОСЫЛКИ:\n");

        // Группируем по причинам
        invalidParcels.stream()
                .collect(Collectors.groupingBy(LoadParcelInvalidDto::causeType))
                .forEach((cause, parcels) -> {
                    sb.append("\n").append(cause).append(":\n");
                    parcels.forEach(invalid -> {
                        ParcelDto parcel = invalid.parcel();
                        sb.append(invalid.causeType().getDescription()).append("\n");
                        sb.append(PrintStringUtil.parcelRender(parcel));
                        sb.append("\n");
                    });
                });

        return sb.toString();
    }

    /**
     * Выводит статистику обработки посылок.
     *
     * @param statistic статистика загрузки
     * @return строковое представление статистики
     */
    public static String renderLoadStatistic(LoadStatisticDto statistic) {
        List<LoadParcelInvalidDto> importParcelInvalids = statistic.invalidParcels();

        StringBuilder sb = new StringBuilder();

        sb.append("\nСТАТИСТИКА:\n");

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

        sb.append(String.format("Всего обработано посылок: %d. Из них: %n", statistic.totalInputParcels()));
        sb.append(String.format("Не прошли валидацию: %d%n", invalidCount));
        sb.append(String.format("Не поместились в машину: %d%n", oversizedCount));
        sb.append(String.format("Не хватило машин: %d%n", noMachineCount));
        sb.append(String.format("Успешно упаковано: %d%n", statistic.totalSuccessLoadParcels()));
        sb.append(String.format("Использовано машин: %d%n", statistic.totalUsedMachines()));

        sb.append("\nБИЛЛИНГ:\n");
        sb.append(String.format("Загружено сегментов: %d%n", statistic.totalSegments()));
        sb.append(String.format("Цена загрузки одного сегмента: %.2f%n", statistic.priceSegment()));
        sb.append(String.format("Стоимость погрузки: %.2f%n", statistic.totalAmount()));

        return sb.toString();
    }

    /**
     * Форматирует список названий посылок в текстовый формат.
     *
     * @param parcels список посылок
     * @return строковое представление названий посылок
     */
    public String renderParcelsNameToText(List<ParcelDto> parcels) {
        if (parcels.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (ParcelDto parcel : parcels) {
            // Добавляем строки посылки
            sb.append(parcel.name()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Форматирует отчёт о разгрузке машин.
     *
     * @param response результат разгрузки
     * @return отформатированный отчёт
     */
    public String renderUnloadResponse(UnloadResponseDto response) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n").append("=".repeat(60)).append("\n");
        sb.append("ОТЧЁТ ПО РАЗГРУЗКЕ МАШИН").append("\n");
        sb.append("=".repeat(60)).append("\n");

        // Общая статистика
        sb.append(renderUnloadStatistics(response.statistic()));

        // Загруженные посылки
        List<ParcelDto> parcels = response.parcels();
        if (parcels != null) {
            sb.append(printLoadedParcels(parcels));
        }

        sb.append("=".repeat(60));

        return sb.toString();
    }

    /**
     * Форматирует статистику разгрузки.
     *
     * @param statistic статистика разгрузки
     * @return строковое представление статистики
     */
    private String renderUnloadStatistics(UnloadStatisticDto statistic) {
        return "\nСТАТИСТИКА:\n"
                + String.format("Успешно разгруженные машины: %d%n", statistic.totalUnloadMachines())
                + String.format("Успешно разгруженные посылки: %d%n", statistic.totalSuccessUnloadParcels())
                + "\nБИЛЛИНГ:\n"
                + String.format("Разгружено сегментов: %d%n", statistic.totalSegments())
                + String.format("Цена загрузки одного сегмента: %.2f%n", statistic.priceSegment())
                + String.format("Стоимость погрузки: %.2f%n", statistic.totalAmount());
    }

    /**
     * Форматирует список разгруженных посылок.
     *
     * @param parcels список посылок
     * @return строковое представление разгруженных посылок
     */
    private String printLoadedParcels(List<ParcelDto> parcels) {
        if (parcels.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n").append("=".repeat(40)).append("\n");
        sb.append("РАЗГРУЖЕННЫЕ ПОСЫЛКИ:\n");

        for (int i = 0; i < parcels.size(); i++) {
            ParcelDto parcel = parcels.get(i);
            sb.append("\n").append("-".repeat(30)).append("\n");
            sb.append("Посылка #").append(i + 1).append("\n");
            sb.append("-".repeat(30)).append("\n");
            sb.append(parcelRender(parcel));
        }

        return sb.toString();
    }

    /**
     * Форматирует историю биллинга для вывода.
     *
     * @param billingDtoList список записей биллинга
     * @return отформатированная история биллинга
     */
    public String formatBillingHistory(List<BillingDto> billingDtoList) {
        StringBuilder sb = new StringBuilder();
        sb.append("История биллинга:\n\n");

        // Форматируем каждую запись
        for (BillingDto billingDto : billingDtoList) {
            sb.append(formatBillingRecord(billingDto));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Форматирует одну запись биллинга.
     *
     * @param billingDto запись биллинга
     * @return отформатированная строка записи
     */
    private String formatBillingRecord(BillingDto billingDto) {
        return String.format(
                "%s; %s; %d машин; %d посылок; %.2f рублей",
                billingDto.createdDt().format(DATE_FORMATTER),
                billingDto.operationType(),
                billingDto.machineCount(),
                billingDto.parcelCount(),
                billingDto.totalAmount());
    }

    /**
     * Возвращает максимальную X координату, занимаемую посылкой.
     *
     * @param parcel DTO посылки
     * @param placed информация о размещении
     * @return максимальная X координата
     */
    private Integer calcParcelPlacedMaxX(ParcelDto parcel, PlacedParcelDto placed) {
        return placed.x() + parcel.width() - 1;
    }

    /**
     * Возвращает максимальную Y координату, занимаемую посылкой.
     *
     * @param parcel DTO посылки
     * @param placed информация о размещении
     * @return максимальная Y координата
     */
    private Integer calcParcelPlacedMaxY(ParcelDto parcel, PlacedParcelDto placed) {
        return placed.y() + parcel.height() - 1;
    }
}
