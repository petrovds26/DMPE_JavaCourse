package ru.hofftech.shared.model.core;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Модель посылки, содержащая её двумерное представление.
 * Посылка представляет собой фигуру, составленную из клеток,
 * заполненных определённым символом.
 */
@Builder
public record Parcel(boolean[][] grid, @Getter Character symbol, @Getter int height, @Getter int width) {

    /**
     * Возвращает строковое представление посылки в виде списка строк.
     * Строки идут снизу вверх (соответствует внутреннему представлению grid).
     *
     * @return список строк посылки
     */
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
     * Возвращает строковое представление посылки в формате,
     * используемом во входных/выходных данных.
     * Строки идут сверху вниз для удобства отображения.
     *
     * @return строковое представление посылки
     */
    public String getForm() {
        List<String> lines = new ArrayList<>(getLines());
        Collections.reverse(lines);
        return String.join("\n", lines);
    }

    /**
     * Строковое представление посылки
     */
    @Override
    public String toString() {
        return String.join("\n", getForm());
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
