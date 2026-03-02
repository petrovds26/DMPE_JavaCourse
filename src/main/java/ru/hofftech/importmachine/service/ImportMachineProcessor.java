package ru.hofftech.importmachine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.importmachine.model.core.ImportMachineResult;
import ru.hofftech.importmachine.model.params.ImportMachineParams;
import ru.hofftech.importmachine.service.output.ImportMachineOutput;
import ru.hofftech.importmachine.service.parser.source.ImportMachineFileSource;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.PlacedParcel;
import ru.hofftech.shared.model.dto.MachineDto;
import ru.hofftech.shared.util.MapperUtil;

import java.util.List;

/**
 * Оркестратор процесса разгрузки машин.
 * Отвечает за последовательное выполнение операций:
 * <ol>
 *   <li>Загрузка машин из файла</li>
 *   <li>Преобразование DTO в сущности</li>
 *   <li>Извлечение всех посылок из машин</li>
 *   <li>Сбор результата и его вывод</li>
 * </ol>
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class ImportMachineProcessor {

    private final ImportMachineParams importMachineParams;
    private final ImportMachineFileSource<String> fileMachineSource;
    private final ImportMachineOutput importMachineOutput;

    /**
     * Выполняет полный цикл обработки машин.
     * <p>
     * Последовательность шагов:
     * <ul>
     *   <li>Загрузка DTO машин из файла</li>
     *   <li>Преобразование DTO в сущности {@link Machine}</li>
     *   <li>Извлечение всех посылок из машин</li>
     *   <li>Формирование результата {@link ImportMachineResult}</li>
     *   <li>Вывод результата через {@link ImportMachineOutput}</li>
     * </ul>
     *
     * @return результат обработки или null в случае ошибки
     */
    public ImportMachineResult process() {
        try {
            // Шаг 1: получаем блоки строк из источника
            List<MachineDto> machinesDto = fileMachineSource.getMachines(importMachineParams.inputFilePath());
            log.info("Обработка файла завершена. Загружено {} машин", machinesDto.size());
            // Шаг 2: преобразуем DTO в Machine
            List<Machine> machines = MapperUtil.dtoToMachines(machinesDto);
            // Шаг 3: Вытащим все посылки из всех машин
            List<Parcel> parcels = machines.stream()
                    .flatMap(machine -> machine.parcels().stream())
                    .map(PlacedParcel::parcel)
                    .toList();
            // Шаг 4: Собираем результат
            ImportMachineResult result = ImportMachineResult.builder()
                    .inputMachines(machines)
                    .parcels(parcels)
                    .build();

            importMachineOutput.output(result, importMachineParams.outputFilePath());

            return result;
        } catch (Exception e) {
            log.error("Ошибка при обработке данных из {}: {}", fileMachineSource.getDescription(), e.getMessage(), e);
        }

        return null;
    }
}
