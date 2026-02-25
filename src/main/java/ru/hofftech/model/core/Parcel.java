package ru.hofftech.model.core;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Модель посылки, содержащая двумерное представление
 *
 * @param grid  Строки, из которых состоит посылка
 * @param symbol Символ, из которого состоит посылка
 */
@Builder
public record Parcel(boolean[][] grid, @Getter Character symbol, @Getter int height, @Getter int width) {

    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < width; j++) {
                if (grid[i][j]) {
                    line.append(symbol);
                } else {
                    line.append(' ');
                }
            }
            lines.add(line.toString());
        }

        return lines;
    }

    /**
     * Строковое представление посылки
     */
    @Override
    public String toString() {
        return String.join("\n", getLines());
    }

    // Переопределяем equals для корректного сравнения содержимого массивов
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parcel that = (Parcel) o;

        // Сравниваем примитивные поля
        if (height != that.height || width != that.width) return false;

        // Сравниваем символы
        if (!Objects.equals(symbol, that.symbol)) return false;

        // Сравниваем содержимое двумерного массива
        return Arrays.deepEquals(grid, that.grid);
    }

    // Переопределяем hashCode для соответствия equals
    @Override
    public int hashCode() {
        int result = Objects.hash(symbol, height, width);
        result = 31 * result + Arrays.deepHashCode(grid);
        return result;
    }
}
