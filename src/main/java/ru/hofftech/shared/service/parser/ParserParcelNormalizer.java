package ru.hofftech.shared.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Отвечает за нормализацию строк посылки.
 */
@Slf4j
public class ParserParcelNormalizer {

    /**
     * Нормализует строки посылки:
     * 1. Обрезает пробелы справа
     * 2. Выравнивает все строки до одинаковой длины
     *
     * @param lines исходные строки
     * @return нормализованные строки
     */
    @NonNull
    public List<String> normalize(@NonNull List<String> lines) {
        log.debug("Нормализация посылки из {} строк", lines.size());

        // Шаг 1: обрезаем пробелы справа
        List<String> trimmedLines = lines.stream().map(String::stripTrailing).toList();

        // Шаг 2: находим максимальную длину
        int maxLength = trimmedLines.stream().mapToInt(String::length).max().orElse(0);

        // Шаг 3: дополняем пробелами справа
        return trimmedLines.stream().map(line -> padRight(line, maxLength)).toList();
    }

    /**
     * Дополняет строку пробелами справа до указанной длины.
     *
     * @param text исходная строка
     * @param length требуемая длина
     * @return дополненная строка
     */
    @NonNull
    private String padRight(@NonNull String text, int length) {
        if (text.length() >= length) {
            return text;
        }
        return text + " ".repeat(length - text.length());
    }
}
