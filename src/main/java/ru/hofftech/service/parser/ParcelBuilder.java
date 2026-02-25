package ru.hofftech.service.parser;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.model.core.Parcel;

import java.util.List;

/**
 * Отвечает за создание Parcel из нормализованных строк
 */
@Slf4j
public class ParcelBuilder {

    /**
     * Создаёт посылку из нормализованных строк
     */
    public Parcel buildFromLines(List<String> normalizedLines) {
        int height = normalizedLines.size();
        int width = normalizedLines.getFirst().length();
        Character symbol = findSymbol(normalizedLines.getFirst());

        boolean[][] grid = new boolean[height][width];

        for (int i = 0; i < height; i++) {
            char[] chars = normalizedLines.get(height - 1 - i).toCharArray();
            for (int j = 0; j < width; j++) {
                grid[i][j] = chars[j] != ' ';
            }
        }

        Parcel parcel = Parcel.builder()
                .grid(grid)
                .symbol(symbol)
                .width(width)
                .height(height)
                .build();

        log.debug("Создана посылка {}x{} с символом '{}'", width, height, symbol);
        return parcel;
    }

    private Character findSymbol(String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (!Character.isWhitespace(c)) {
                return c;
            }
        }
        return null;
    }
}
