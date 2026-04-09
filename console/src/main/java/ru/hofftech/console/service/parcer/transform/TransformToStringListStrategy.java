package ru.hofftech.console.service.parcer.transform;

import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Интерфейс стратегии преобразования источника в список строк.
 * <p>
 * Каждая стратегия отвечает за преобразование определённого типа источника
 * (текст, файл) в список строк.
 */
@NullMarked
public interface TransformToStringListStrategy {
    /**
     * Преобразует источник в список строк.
     *
     * @param source источник данных (текст или путь к файлу)
     * @return список строк
     */
    List<String> transform(String source);
}
