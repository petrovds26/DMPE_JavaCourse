package ru.hofftech.unload.service.output.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.util.JsonUtil;
import ru.hofftech.unload.model.core.UnloadResult;
import ru.hofftech.unload.model.dto.UnloadResultDto;
import ru.hofftech.unload.service.output.UnloadPrepareOutputResult;
import ru.hofftech.unload.util.UnloadMapperUtil;

/**
 * Реализация подготовки вывода результатов в формате JSON.
 */
@Slf4j
@RequiredArgsConstructor
public class UnloadPrepareOutputResultJson implements UnloadPrepareOutputResult {

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult output(@NonNull UnloadResult result) {
        // Преобразуем результат в DTO для вывода
        UnloadResultDto outputDto = UnloadMapperUtil.loadingResultToOutputDto(result);

        // Сериализуем в JSON
        String json = JsonUtil.toJson(outputDto);

        return ProcessorCommandResult.builder().success(true).message(json).build();
    }
}
