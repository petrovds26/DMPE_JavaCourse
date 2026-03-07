package ru.hofftech.importparcel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importparcel.model.core.ImportParcelInvalid;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.model.enums.ImportParcelInvalidCauseType;
import ru.hofftech.importparcel.model.params.ImportParcelParams;
import ru.hofftech.importparcel.service.loader.strategy.ParcelLoadingStrategy;
import ru.hofftech.importparcel.service.output.ImportParcelOutput;
import ru.hofftech.importparcel.service.parser.machine.source.ImportParcelMachineSource;
import ru.hofftech.importparcel.service.parser.parcel.source.ImportParcelFileSource;
import ru.hofftech.importparcel.validation.impl.ParcelGridValidator;
import ru.hofftech.importparcel.validation.impl.ParcelListStringValidator;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.service.parser.ParcelBuilder;
import ru.hofftech.shared.service.parser.ParcelNormalizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Оркестратор процесса упаковки посылок в машины.
 * Отвечает за последовательное выполнение операций:
 * <ol>
 *   <li>Создание машин по заданному количеству</li>
 *   <li>Загрузка и валидация посылок из файла</li>
 *   <li>Нормализация и создание сущностей посылок</li>
 *   <li>Упаковка посылок в машины по выбранной стратегии</li>
 *   <li>Сбор результата и его вывод</li>
 * </ol>
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class ImportParcelProcessor {

    @NonNull
    private final ImportParcelParams importParcelParams;

    @NonNull
    private final ImportParcelMachineSource<Integer> importParcelMachineSource;

    @NonNull
    private final ImportParcelFileSource<String> fileParcelSource;

    @NonNull
    private final ParcelNormalizer normalizer;

    @NonNull
    private final ParcelBuilder parcelBuilder;

    @NonNull
    private final ParcelListStringValidator stringValidator;

    @NonNull
    private final ParcelGridValidator gridValidator;

    @NonNull
    private final ParcelLoadingStrategy loadingStrategy;

    @NonNull
    private final ImportParcelOutput importParcelOutput;

    /**
     * Выполняет полный цикл обработки посылок.
     * <p>
     * Последовательность шагов:
     * <ul>
     *   <li>Создание указанного количества пустых машин</li>
     *   <li>Загрузка и разбиение файла на блоки посылок</li>
     *   <li>Валидация и нормализация каждой посылки</li>
     *   <li>Упаковка валидных посылок по выбранной стратегии</li>
     *   <li>Сбор статистики и ошибок</li>
     *   <li>Вывод результата</li>
     * </ul>
     *
     * @return результат упаковки или null в случае критической ошибки
     */
    @Nullable
    public ImportParcelResult process() {
        try {
            List<Parcel> validParcels = new ArrayList<>();
            List<ImportParcelInvalid> importParcelInvalids = new ArrayList<>();
            List<Parcel> inputParcels = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            // Шаг 0: Загружаем машины
            List<Machine> inputMachines = importParcelMachineSource.getMachines(importParcelParams.truckCount());

            if (inputMachines.isEmpty()) {
                String cause = "Не удалось загрузить ни одну машину";
                errors.add(cause);
                log.error(cause);
            }

            // Шаг 1: получаем блоки строк из источника
            List<List<String>> blocks = fileParcelSource.getParcelBlocks(importParcelParams.inputFilePath());

            log.info("Начало обработки данных из {}", fileParcelSource.getDescription());

            // Шаг 2: обрабатываем каждый блок
            for (int i = 0; i < blocks.size(); i++) {
                List<String> rawLines = blocks.get(i);
                log.debug("Обработка посылки #{} ({} строк)", i + 1, rawLines.size());

                // Шаг 3: валидация сырых строк
                List<String> stringErrors = stringValidator.validate(rawLines);
                if (!stringErrors.isEmpty()) {
                    String cause = String.format(
                            "Посылка #%d отклонена на этапе валидации строк. Ошибки: %s", i + 1, stringErrors);
                    errors.add(cause);
                    log.warn(cause);
                    continue;
                }

                // Шаг 4: нормализация
                List<String> normalizedLines = normalizer.normalize(rawLines);

                // Шаг 5: создание посылки
                Parcel parcel = parcelBuilder.buildFromLines(normalizedLines);
                inputParcels.add(parcel);

                // Шаг 6: валидация готовой посылки
                List<String> gridErrors = gridValidator.validate(parcel);

                if (gridErrors.isEmpty()) {
                    validParcels.add(parcel);
                    log.debug("Посылка #{} успешно загружена", i + 1);
                } else {
                    String cause = String.format(
                            "Посылка #%d отклонена на этапе валидации grid. Ошибки: %s", i + 1, gridErrors);
                    importParcelInvalids.add(ImportParcelInvalid.builder()
                            .parcel(parcel)
                            .causeType(ImportParcelInvalidCauseType.PARCEL_INVALID)
                            .cause(cause)
                            .build());
                    log.warn(cause);
                }
            }

            log.info("Обработка файла завершена. Загружено {}/{} посылок", validParcels.size(), blocks.size());

            // Шаг 7: запуск расчета загрузки машин
            log.debug(
                    "Начало упаковки {} посылок по алгоритму: {}",
                    validParcels.size(),
                    loadingStrategy.getAlgorithmName());

            ImportParcelResult packingResult = loadingStrategy.loadParcels(validParcels, inputMachines);

            if (packingResult.importParcelInvalids() != null) {
                importParcelInvalids.addAll(packingResult.importParcelInvalids());
            }
            if (packingResult.errors() != null) {
                errors.addAll(packingResult.errors());
            }

            ImportParcelResult extendedResult = ImportParcelResult.builder()
                    .machines(packingResult.machines())
                    .importParcelInvalids(importParcelInvalids)
                    .inputParcels(inputParcels)
                    .errors(errors)
                    .build();

            // Шаг 9: выводим результат
            importParcelOutput.output(extendedResult, importParcelParams.outputFilePath());

            return extendedResult;
        } catch (Exception e) {
            log.error("Ошибка при обработке данных из {}: {}", fileParcelSource.getDescription(), e.getMessage(), e);
        }

        return null;
    }
}
