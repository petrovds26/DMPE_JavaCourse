package ru.hofftech.load.service.output.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.load.model.dto.LoadParcelOutputResultDto;
import ru.hofftech.load.service.output.LoadPrepareOutputResult;
import ru.hofftech.load.util.LoadParcelMapperUtil;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.util.JsonUtil;

/**
 * Реализация подготовки вывода результатов в формате JSON.
 */
@Slf4j
@RequiredArgsConstructor
@NullMarked
public class LoadPrepareOutputResultJson implements LoadPrepareOutputResult {

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult output(LoadResult result) {
        // Преобразуем результат в DTO для вывода
        LoadParcelOutputResultDto outputDto = LoadParcelMapperUtil.loadingResultToOutputDto(result);

        // Сериализуем в JSON
        String json = JsonUtil.toJson(outputDto);

        return ProcessorCommandResult.createSuccess(json);
    }
}
