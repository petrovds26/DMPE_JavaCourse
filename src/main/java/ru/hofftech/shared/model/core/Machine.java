package ru.hofftech.shared.model.core;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Модель машины (кузова) для размещения посылок.
 * Представляет собой двумерную сетку заданного размера,
 * в которой можно размещать посылки с проверкой пересечений.
 *
 * @param grid текущее состояние для быстрой проверки
 * @param parcels список размещённых посылок с координатами
 * @param width ширина машины
 * @param height высота машины
 *
 */
@Builder
public record Machine(
        char[][] grid, // Текущее состояние для быстрой проверки
        @NonNull List<PlacedParcel> parcels, // Список размещённых посылок с координатами
        int width,
        int height) {
    public static final int DEFAULT_WIDTH = 6;
    public static final int DEFAULT_HEIGHT = 6;

    /**
     * Конструктор для создания машины произвольного размера.
     *
     * @param width ширина машины
     * @param height высота машины
     */
    public Machine(int width, int height) {
        this(createEmptyGrid(width, height), new ArrayList<>(), width, height);
    }

    /**
     * Конструктор для создания машины с размерами по умолчанию (6x6).
     */
    public Machine() {
        this(createEmptyGrid(DEFAULT_WIDTH, DEFAULT_HEIGHT), new ArrayList<>(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Возвращает строковое представление машины в виде списка строк.
     * Строки идут снизу вверх (первая строка - низ машины).
     *
     * @return список строк, представляющих сетку машины (не может быть null)
     */
    @NonNull
    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        for (int i = height - 1; i >= 0; i--) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < width; j++) {
                line.append(grid[i][j]);
            }
            lines.add(line.toString());
        }
        return lines;
    }

    /**
     * Проверяет, занято ли место под посылку в указанной позиции.
     * Учитывает выход за границы и пересечение с уже размещёнными посылками.
     *
     * @param parcel посылка для размещения (не может быть null)
     * @param startX координата X левого нижнего угла
     * @param startY координата Y левого нижнего угла
     * @return true если место занято или выходит за границы
     */
    public boolean isPlaceOccupied(@NonNull Parcel parcel, int startX, int startY) {
        int parcelHeight = parcel.getHeight();
        int parcelWidth = parcel.getWidth();

        // Проверка границ
        if (startX + parcelWidth > width || startY + parcelHeight > height) {
            return true;
        }

        // Проверка, что все клетки свободны
        for (int i = 0; i < parcelHeight; i++) {
            for (int j = 0; j < parcelWidth; j++) {
                if (parcel.grid()[i][j] && grid[startY + i][startX + j] != ' ') {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Размещает посылку в указанной позиции.
     * Создаёт новую машину с размещённой посылкой.
     *
     * @param parcel посылка для размещения (не может быть null)
     * @param startX координата X левого нижнего угла
     * @param startY координата Y левого нижнего угла
     * @return новая машина с размещённой посылкой (не может быть null)
     * @throws IllegalArgumentException если место занято или выходит за границы
     */
    @NonNull
    public Machine placeParcel(@NonNull Parcel parcel, int startX, int startY) {
        if (isPlaceOccupied(parcel, startX, startY)) {
            throw new IllegalArgumentException("Невозможно разместить посылку в указанной позиции");
        }

        // Создаём копию grid
        char[][] newGrid = copyGrid();
        List<PlacedParcel> newPlacedParcels = new ArrayList<>(parcels);

        // Размещаем посылку в grid
        int parcelHeight = parcel.getHeight();
        int parcelWidth = parcel.getWidth();
        char symbol = parcel.symbol();

        for (int i = 0; i < parcelHeight; i++) {
            for (int j = 0; j < parcelWidth; j++) {
                if (parcel.grid()[i][j]) {
                    newGrid[startY + i][startX + j] = symbol;
                }
            }
        }

        // Добавляем информацию о размещённой посылке
        PlacedParcel placedParcel =
                PlacedParcel.builder().parcel(parcel).x(startX).y(startY).build();
        newPlacedParcels.add(placedParcel);

        return Machine.builder()
                .grid(newGrid)
                .parcels(newPlacedParcels)
                .width(width)
                .height(height)
                .build();
    }

    /**
     * Проверяет, влезает ли посылка в машину по габаритам.
     * Не учитывает занятость клеток, только размеры.
     *
     * @param parcel посылка для проверки (не может быть null)
     * @return true если посылка по размерам помещается в машину
     */
    public boolean fitsInMachine(@NonNull Parcel parcel) {
        return parcel.getWidth() <= width() && parcel.getHeight() <= height();
    }

    /**
     * Возвращает строковое представление машины для отладки.
     *
     * @return строковое представление машины (не может быть null)
     */
    @Override
    @NonNull
    public String toString() {
        return String.format("Machine{parcels=%d, size=%dx%d}", parcels.size(), width, height);
    }

    /**
     * Сравнивает машины по содержимому.
     *
     * @param o объект для сравнения (может быть null)
     * @return true если объекты равны
     */
    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Machine machine = (Machine) o;

        // Сравниваем примитивные поля
        if (width != machine.width || height != machine.height) return false;

        // Сравниваем списки посылок
        if (!Objects.equals(parcels, machine.parcels)) return false;

        // Сравниваем содержимое двумерного массива
        return Arrays.deepEquals(grid, machine.grid);
    }

    /**
     * Вычисляет хеш-код машины.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(parcels, width, height);
        result = 31 * result + Arrays.deepHashCode(grid);
        return result;
    }

    /**
     * Создаёт пустую сетку указанного размера.
     *
     * @param width ширина сетки
     * @param height высота сетки
     * @return пустая сетка (не может быть null)
     */
    private static char @NonNull [] @NonNull [] createEmptyGrid(int width, int height) {
        char[][] grid = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = ' ';
            }
        }
        return grid;
    }

    /**
     * Создаёт копию текущей сетки.
     *
     * @return копия сетки (не может быть null)
     */
    private char @NonNull [] @NonNull [] copyGrid() {
        char[][] copy = new char[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, width);
        }
        return copy;
    }
}
