package ru.hofftech.shared.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Parcel;

import java.util.List;

/**
 * Отвечает за создание Parcel из нормализованных строк
 */
@Slf4j
public class ParserParcelBuilder {

    /**
     * Создаёт посылку из нормализованных строк с автоматическим определением символа.
     *
     * @param name название посылки
     * @param normalizedLines нормализованные строки формы
     * @return созданная посылка
     */
    @NonNull
    public Parcel buildFromLines(@NonNull String name, @NonNull List<String> normalizedLines) {
        char symbol = findSymbol(normalizedLines.getFirst());

        return buildFromLines(name, normalizedLines, symbol);
    }

    /**
     * Создаёт посылку из нормализованных строк с указанным символом.
     *
     * @param name название посылки
     * @param normalizedLines нормализованные строки формы
     * @param symbol символ посылки
     * @return созданная посылка
     */
    @NonNull
    public Parcel buildFromLines(@NonNull String name, @NonNull List<String> normalizedLines, @NonNull String symbol) {
        return buildFromLines(name, normalizedLines, symbol.charAt(0));
    }

    /**
     * Создаёт посылку из нормализованных строк.
     *
     * @param name название посылки
     * @param normalizedLines нормализованные строки формы
     * @param symbol символ посылки
     * @return созданная посылка
     */
    @NonNull
    private Parcel buildFromLines(@NonNull String name, @NonNull List<String> normalizedLines, char symbol) {
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
     * @param line строка для анализа
     * @return первый непробельный символ или 'X' по умолчанию
     */
    private char findSymbol(@NonNull String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (!Character.isWhitespace(c)) {
                return c;
            }
        }
        return 'X';
    }
}
