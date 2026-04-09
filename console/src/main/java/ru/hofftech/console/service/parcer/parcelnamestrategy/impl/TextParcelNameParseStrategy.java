package ru.hofftech.console.service.parcer.parcelnamestrategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import ru.hofftech.console.model.enums.LoadInputParcelType;
import ru.hofftech.console.service.parcer.parcelnamestrategy.ParcelNameParseStrategy;
import ru.hofftech.console.service.parcer.transform.impl.TransformTextToStringList;
import ru.hofftech.shared.model.dto.newdto.ParcelNameRequestDto;

import java.util.List;

/**
 * Стратегия парсинга названий посылок из текстовой строки.
 * <p>
 * Ожидает, что названия посылок разделены символами перевода строки.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@NullMarked
public class TextParcelNameParseStrategy implements ParcelNameParseStrategy {

    private final TransformTextToStringList transformer;

    /**
     * {@inheritDoc}
     */
    @Override
    public LoadInputParcelType getSupportedType() {
        return LoadInputParcelType.TEXT;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Разбивает текстовую строку по символам перевода строки,
     * обрезает пробелы и преобразует в список ParcelNameRequestDto.
     */
    @Override
    public List<ParcelNameRequestDto> parse(String source) {
        log.debug("Парсинг текстового ввода");
        return transformer.transform(source).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(ParcelNameRequestDto::new)
                .toList();
    }
}
