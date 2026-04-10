package ru.hofftech.console.service.parcer.parcelnamestrategy;

import org.jspecify.annotations.NullMarked;
import ru.hofftech.console.model.enums.LoadInputParcelType;
import ru.hofftech.shared.model.dto.ParcelNameRequestDto;

import java.util.List;

/**
 * Интерфейс стратегии парсинга названий посылок.
 * <p>
 * Каждая стратегия отвечает за парсинг определённого типа источника
 * (текст, TXT файл, JSON файл).
 */
@NullMarked
public interface ParcelNameParseStrategy {
    /**
     * Возвращает тип источника, который обрабатывает данная стратегия.
     *
     * @return тип источника
     */
    LoadInputParcelType getSupportedType();

    /**
     * Парсит источник и возвращает список DTO названий посылок.
     *
     * @param source источник данных (текст или путь к файлу)
     * @return список DTO названий посылок
     */
    List<ParcelNameRequestDto> parse(String source);
}
