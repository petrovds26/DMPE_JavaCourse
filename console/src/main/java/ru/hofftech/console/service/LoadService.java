package ru.hofftech.console.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.hofftech.console.exception.FeignException;
import ru.hofftech.console.exception.ValidateException;
import ru.hofftech.console.model.enums.LoadOutputType;
import ru.hofftech.console.service.parcer.ParcelParseService;
import ru.hofftech.console.service.parcer.transform.impl.TransformTextToStringList;
import ru.hofftech.console.util.JsonUtil;
import ru.hofftech.console.validation.impl.OutputFilePathValidator;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.LoadRequestDto;
import ru.hofftech.shared.model.dto.LoadResponseDto;
import ru.hofftech.shared.model.dto.MachineFormRequestDto;
import ru.hofftech.shared.model.dto.ParcelNameRequestDto;
import ru.hofftech.shared.model.enums.LoadStrategyType;
import ru.hofftech.shared.util.PrintStringUtil;

import java.util.List;

/**
 * Сервис для выполнения операций загрузки посылок в машины.
 * <p>
 * Обрабатывает запросы на загрузку: парсит входные данные,
 * формирует DTO для Core сервиса и обрабатывает ответ.
 */
@NullMarked
@Service
@RequiredArgsConstructor
@Slf4j
public class LoadService {
    private final ExternalService externalService;
    private final ParcelParseService parcelParseService;
    private final TransformTextToStringList transformTextToStringList;
    private final OutputFilePathValidator outputFilePathValidator;
    private final FileSaveService fileSaveService;

    /**
     * Выполняет загрузку посылок в машины.
     *
     * @param inputParcelFile путь к файлу с посылками
     * @param inputParcelText текст с посылками
     * @param outputFile      путь к выходному файлу
     * @param outputType      тип вывода результата
     * @param strategyId      идентификатор стратегии упаковки
     * @param machines        описание доступных машин
     * @param userId          идентификатор пользователя
     * @return результат выполнения операции
     * @throws ValidateException если не найдены посылки или машины, или указана неверная стратегия
     * @throws FeignException    если при вызове Core сервиса произошла ошибка
     */
    public String loadParcel(
            String inputParcelFile,
            String inputParcelText,
            String outputFile,
            String outputType,
            String strategyId,
            String machines,
            String userId) {
        List<ParcelNameRequestDto> parcelNames = parcelParseService.parseParcels(inputParcelFile, inputParcelText);
        if (parcelNames.isEmpty()) {
            throw new ValidateException("Не найдено ни одной посылки для загрузки");
        }

        List<MachineFormRequestDto> machinesList = parseMachines(machines);
        if (machinesList.isEmpty()) {
            throw new ValidateException("Не найдено ни одной машины для загрузки");
        }

        LoadStrategyType loadStrategyType = LoadStrategyType.fromName(strategyId);

        if (loadStrategyType == null) {
            throw new ValidateException("Стратегия для загрузки %s не найдена. Доступные стратегии: %s"
                    .formatted(strategyId, LoadStrategyType.allStrategies(", ")));
        }

        LoadOutputType loadOutputType = determineLoadOutputType(outputFile, outputType);

        LoadRequestDto loadRequestDto = LoadRequestDto.builder()
                .parcels(parcelNames)
                .machines(machinesList)
                .userId(userId)
                .loadStrategy(loadStrategyType)
                .build();

        Response<LoadResponseDto> response = externalService.loadParcel(loadRequestDto);

        if (response.isSuccess()) {

            return postProcessingResponse(response.getData(), loadOutputType, outputFile);
        }

        throw new FeignException(response);
    }

    /**
     * Определяет тип вывода результата и валидирует выходной файл при необходимости.
     *
     * @param outputFile путь к выходному файлу
     * @param outputType тип вывода
     * @return тип вывода результата
     * @throws ValidateException если тип вывода не найден или путь к файлу невалиден
     */
    private LoadOutputType determineLoadOutputType(String outputFile, String outputType) {
        LoadOutputType loadOutputType = LoadOutputType.fromString(outputType);

        if (loadOutputType == null) {
            throw new ValidateException("Тип вывода %s не найден. Доступные выводы: %s"
                    .formatted(outputType, LoadOutputType.allLoadOutputType(", ")));
        }

        if (loadOutputType.needSaveFile()) {
            List<String> outputErrors = outputFilePathValidator.validate(outputFile);
            if (!outputErrors.isEmpty()) {
                throw new ValidateException(
                        "Ошибки валидации параметра Выходной файл: %s".formatted(String.join("; ", outputErrors)));
            }
        }
        return loadOutputType;
    }

    /**
     * Обрабатывает ответ от Core сервиса в зависимости от типа вывода.
     *
     * @param response       ответ от Core сервиса
     * @param loadOutputType тип вывода результата
     * @param outputFile     путь к выходному файлу
     * @return отформатированный результат или сообщение о сохранении файла
     */
    private String postProcessingResponse(LoadResponseDto response, LoadOutputType loadOutputType, String outputFile) {
        return switch (loadOutputType) {
            case TEXT_FILE -> fileSaveService.saveFile(PrintStringUtil.renderLoadResponse(response), outputFile);
            case JSON_FILE -> fileSaveService.saveFile(JsonUtil.toJson(response), outputFile);
            default -> PrintStringUtil.renderLoadResponse(response);
        };
    }

    /**
     * Парсит строку с описанием машин в список DTO.
     *
     * @param source строка с описанием машин (каждая машина на новой строке)
     * @return список DTO машин
     */
    private List<MachineFormRequestDto> parseMachines(String source) {
        log.debug("Парсинг текстового ввода машин");
        return transformTextToStringList.transform(source).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(MachineFormRequestDto::new)
                .toList();
    }
}
