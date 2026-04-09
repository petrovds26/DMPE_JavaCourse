package ru.hofftech.core.model.core;

import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
@NullMarked
@Builder
public record Parcel(
        boolean[][] grid, @Getter String name, @Getter char symbol, @Getter int height, @Getter int width) {

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
     * Подсчитывает количество заполненных клеток в посылке.
     *
     * @return количество заполненных клеток
     */
    public int getFilledCellsCount() {
        int count = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grid[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Строковое представление посылки
     *
     * @return строковое представление посылки
     */
    @Override
    public String toString() {
        return String.join("\n", getForm());
    }

    /**
     * Сравнивает посылки по содержимому.
     *
     * @param o объект для сравнения
     * @return true если объекты равны
     */
    @Override
    public boolean equals(@Nullable Object o) {
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

    /**
     * Вычисляет хеш-код посылки.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(symbol, height, width);
        result = 31 * result + Arrays.deepHashCode(grid);
        return result;
    }
}
