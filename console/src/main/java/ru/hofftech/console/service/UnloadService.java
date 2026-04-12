package ru.hofftech.console.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.hofftech.console.exception.FeignException;
import ru.hofftech.console.exception.ValidateException;
import ru.hofftech.console.model.enums.UnloadOutputType;
import ru.hofftech.console.service.parcer.transform.impl.TransformFileToStringList;
import ru.hofftech.console.util.JsonUtil;
import ru.hofftech.console.validation.impl.InputFilePathValidator;
import ru.hofftech.console.validation.impl.OutputFilePathValidator;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.LoadResponseDto;
import ru.hofftech.shared.model.dto.MachineDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.UnloadRequestDto;
import ru.hofftech.shared.model.dto.UnloadResponseDto;
import ru.hofftech.shared.util.PrintStringUtil;

import java.util.List;

/**
 * Сервис для выполнения операций разгрузки машин.
 * <p>
 * Обрабатывает запросы на разгрузку: парсит входной JSON файл,
 * формирует DTO для Core сервиса и обрабатывает ответ.
 */
@NullMarked
@Service
@RequiredArgsConstructor
@Slf4j
public class UnloadService {
    private final ExternalService externalService;
    private final OutputFilePathValidator outputFilePathValidator;
    private final FileSaveService fileSaveService;

    private final InputFilePathValidator inputFilePathValidator;
    private final TransformFileToStringList transformFileToStringList;

    /**
     * Выполняет разгрузку машин из JSON файла.
     *
     * @param inputFile  путь к входному JSON файлу
     * @param outputFile путь к выходному файлу (опционально)
     * @param outputType тип вывода результата
     * @param userId     идентификатор пользователя
     * @return результат выполнения операции
     * @throws ValidateException если файл не найден или не содержит машин
     * @throws FeignException    если при вызове Core сервиса произошла ошибка
     */
    public String unloadMachines(String inputFile, String outputFile, String outputType, String userId) {

        log.debug("Разгрузка машин");

        List<MachineDto> machines = parseInputFile(inputFile);

        UnloadOutputType unloadOutputType = determineUnloadOutputType(outputFile, outputType);

        UnloadRequestDto unloadRequestDto =
                UnloadRequestDto.builder().machines(machines).userId(userId).build();

        Response<UnloadResponseDto> response = externalService.unloadParcel(unloadRequestDto);

        if (response.isSuccess()) {

            return postProcessingResponse(response.getData(), unloadOutputType, outputFile);
        }

        throw new FeignException(response);
    }

    /**
     * Парсит входной JSON файл и извлекает список машин.
     *
     * @param inputFile путь к входному файлу
     * @return список DTO машин
     * @throws ValidateException если файл не существует или не содержит машин
     */
    private List<MachineDto> parseInputFile(String inputFile) {
        List<String> inputErrors = inputFilePathValidator.validate(inputFile);
        if (!inputErrors.isEmpty()) {
            throw new ValidateException("Ошибка валидации файла: " + String.join("; ", inputErrors));
        }

        log.debug("Парсинг JSON файла: {}", inputFile);

        List<String> lines = transformFileToStringList.transform(inputFile);
        String jsonContent = String.join("\n", lines);
        LoadResponseDto loadResponseDto = JsonUtil.fromJson(jsonContent, LoadResponseDto.class);

        List<MachineDto> machines = loadResponseDto.machines();

        if (machines == null || machines.isEmpty()) {
            throw new ValidateException("Не найдено ни одной машины для загрузки");
        }

        return machines;
    }

    /**
     * Определяет тип вывода результата и валидирует выходной файл при необходимости.
     *
     * @param outputFile путь к выходному файлу
     * @param outputType тип вывода
     * @return тип вывода результата
     * @throws ValidateException если тип вывода не найден или путь к файлу невалиден
     */
    private UnloadOutputType determineUnloadOutputType(String outputFile, String outputType) {
        UnloadOutputType unloadOutputType = UnloadOutputType.fromString(outputType);

        if (unloadOutputType == null) {
            throw new ValidateException("Тип вывода %s не найден. Доступные выводы: %s"
                    .formatted(outputType, UnloadOutputType.allUnloadOutputType(", ")));
        }

        if (unloadOutputType.needSaveFile()) {
            List<String> outputErrors = outputFilePathValidator.validate(outputFile);
            if (!outputErrors.isEmpty()) {
                throw new ValidateException(
                        "Ошибки валидации параметра Выходной файл: %s".formatted(String.join("; ", outputErrors)));
            }
        }
        return unloadOutputType;
    }

    /**
     * Обрабатывает ответ от Core сервиса в зависимости от типа вывода.
     *
     * @param response         ответ от Core сервиса
     * @param unloadOutputType тип вывода результата
     * @param outputFile       путь к выходному файлу
     * @return отформатированный результат или сообщение о сохранении файла
     */
    private String postProcessingResponse(
            UnloadResponseDto response, UnloadOutputType unloadOutputType, String outputFile) {
        List<ParcelDto> parcels = response.parcels();
        return switch (unloadOutputType) {
            case TEXT_SIMPLE_FILE -> fileSaveService.saveFile(
                    parcels == null ? "" : PrintStringUtil.renderParcelsNameToText(parcels), outputFile);
            case TEXT_FULL_FILE -> fileSaveService.saveFile(PrintStringUtil.renderUnloadResponse(response), outputFile);
            case JSON_FILE -> fileSaveService.saveFile(JsonUtil.toJson(response), outputFile);
            default -> PrintStringUtil.renderUnloadResponse(response);
        };
    }
}
