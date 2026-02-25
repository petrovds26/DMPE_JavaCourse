package ru.hofftech.model.core;

import lombok.Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Модель машины (кузова) размером 6x6
 */
@Builder
public record Machine(
        char[][] grid, // Текущее состояние для быстрой проверки
        List<PlacedParcel> parcels, // Список размещённых посылок с координатами
        int width,
        int height) {
    public static final int DEFAULT_WIDTH = 6;
    public static final int DEFAULT_HEIGHT = 6;

    /**
     * Конструктор по умолчанию создаёт пустую машину 6x6
     */
    public Machine() {
        this(createEmptyGrid(), new ArrayList<>(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private static char[][] createEmptyGrid() {
        char[][] grid = new char[DEFAULT_HEIGHT][DEFAULT_WIDTH];
        for (int i = 0; i < DEFAULT_HEIGHT; i++) {
            for (int j = 0; j < DEFAULT_WIDTH; j++) {
                grid[i][j] = ' ';
            }
        }
        return grid;
    }

    /**
     * Проверяет, занято ли место под посылку в указанной позиции
     * @return true если место занято или выходит за границы
     */
    public boolean isPlaceOccupied(Parcel parcel, int startX, int startY) {
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
     * Размещает посылку в указанной позиции
     */
    public Machine placeParcel(Parcel parcel, int startX, int startY) {
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

    private char[][] copyGrid() {
        char[][] copy = new char[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, width);
        }
        return copy;
    }

    @Override
    public String toString() {
        return String.format("Machine{parcels=%d, size=%dx%d}", parcels.size(), width, height);
    }

    // Переопределяем equals для корректного сравнения содержимого массивов
    @Override
    public boolean equals(Object o) {
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

    // Переопределяем hashCode для соответствия equals
    @Override
    public int hashCode() {
        int result = Objects.hash(parcels, width, height);
        result = 31 * result + Arrays.deepHashCode(grid);
        return result;
    }
}
