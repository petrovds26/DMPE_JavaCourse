package ru.hofftech.service.parser;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Отвечает за нормализацию строк посылки
 */
@Slf4j
public class ParcelNormalizer {

    /**
     * Нормализует строки посылки:
     * 1. Обрезает пробелы справа
     * 2. Выравнивает все строки до одинаковой длины
     */
    public List<String> normalize(List<String> lines) {
        log.debug("Нормализация посылки из {} строк", lines.size());

        // Шаг 1: обрезаем пробелы справа
        List<String> trimmedLines = lines.stream().map(String::stripTrailing).toList();

        // Шаг 2: находим максимальную длину
        int maxLength = trimmedLines.stream().mapToInt(String::length).max().orElse(0);

        // Шаг 3: дополняем пробелами справа
        return trimmedLines.stream().map(line -> padRight(line, maxLength)).toList();
    }

    private String padRight(String text, int length) {
        if (text.length() >= length) {
            return text;
        }
        return text + " ".repeat(length - text.length());
    }
}
