package ru.hofftech.core.service.parcer;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.hofftech.core.model.core.Parcel;

import java.util.List;

/**
 * Отвечает за создание Parcel из нормализованных строк.
 */
@Slf4j
@NullMarked
@Service
public class ParserParcelBuilder {

    /**
     * Создаёт посылку из нормализованных строк с автоматическим определением символа.
     *
     * @param name            название посылки (не может быть null)
     * @param normalizedLines нормализованные строки формы (не может быть null)
     * @return созданная посылка (не может быть null)
     */
    public Parcel buildFromLines(String name, List<String> normalizedLines) {
        char symbol = findSymbol(normalizedLines.getFirst());

        return buildFromLines(name, normalizedLines, symbol);
    }

    /**
     * Создаёт посылку из нормализованных строк с указанным символом.
     *
     * @param name            название посылки (не может быть null)
     * @param normalizedLines нормализованные строки формы (не может быть null)
     * @param symbol          символ посылки (не может быть null)
     * @return созданная посылка (не может быть null)
     */
    public Parcel buildFromLines(String name, List<String> normalizedLines, String symbol) {
        return buildFromLines(name, normalizedLines, symbol.charAt(0));
    }

    /**
     * Создаёт посылку из нормализованных строк.
     *
     * @param name            название посылки (не может быть null)
     * @param normalizedLines нормализованные строки формы (не может быть null)
     * @param symbol          символ посылки
     * @return созданная посылка (не может быть null)
     */
    private Parcel buildFromLines(String name, List<String> normalizedLines, char symbol) {
        int height = normalizedLines.size();
        int width = normalizedLines.getFirst().length();

        boolean[][] grid = new boolean[height][width];

        for (int i = 0; i < height; i++) {
            char[] chars = normalizedLines.get(height - 1 - i).toCharArray();
            for (int j = 0; j < width; j++) {
                grid[i][j] = chars[j] != ' ';
            }
        }

        Parcel parcel = Parcel.builder()
                .name(name)
                .grid(grid)
                .symbol(symbol)
                .width(width)
                .height(height)
                .build();

        log.debug("Создана посылка {}x{} с символом '{}'", width, height, symbol);
        return parcel;
    }

    /**
     * Находит первый непробельный символ в строке.
     *
     * @param line строка для анализа (не может быть null)
     * @return первый непробельный символ или 'X' по умолчанию
     */
    private char findSymbol(String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (!Character.isWhitespace(c)) {
                return c;
            }
        }
        return 'X';
    }
}
