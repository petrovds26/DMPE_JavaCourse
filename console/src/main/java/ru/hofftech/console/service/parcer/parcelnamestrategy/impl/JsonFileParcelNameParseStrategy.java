package ru.hofftech.console.service.parcer.parcelnamestrategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import ru.hofftech.console.model.enums.LoadInputParcelType;
import ru.hofftech.console.service.parcer.parcelnamestrategy.ParcelNameParseStrategy;
import ru.hofftech.console.service.parcer.transform.impl.TransformFileToStringList;
import ru.hofftech.console.util.JsonUtil;
import ru.hofftech.shared.model.dto.ParcelNameRequestDto;
import ru.hofftech.shared.model.dto.ParcelsNameDto;

import java.util.List;

/**
 * Стратегия парсинга названий посылок из JSON файла.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@NullMarked
public class JsonFileParcelNameParseStrategy implements ParcelNameParseStrategy {

    private final TransformFileToStringList transformer;
    /**
     * {@inheritDoc}
     */
    @Override
    public LoadInputParcelType getSupportedType() {
        return LoadInputParcelType.JSON_FILE;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Читает JSON файл, парсит его в ParcelsNameDto и преобразует в список ParcelNameRequestDto.
     */
    @Override
    public List<ParcelNameRequestDto> parse(String source) {
        log.debug("Парсинг JSON файла: {}", source);

        List<String> lines = transformer.transform(source);
        String jsonContent = String.join("\n", lines);
        ParcelsNameDto dto = JsonUtil.fromJson(jsonContent, ParcelsNameDto.class);

        // 4. Преобразуем в список DTO
        return dto.parcelsName().stream()
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(ParcelNameRequestDto::new)
                .toList();
    }
}
