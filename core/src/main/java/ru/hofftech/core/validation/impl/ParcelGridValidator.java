package ru.hofftech.core.validation.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.hofftech.core.model.core.Parcel;
import ru.hofftech.core.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Валидатор для готовой Parcel.
 * Проверяет:
 * - Одинаковую ширину строк
 * - Связность посылки
 */
@Slf4j
@NullMarked
@Service
public class ParcelGridValidator implements Validator<Parcel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> validate(@Nullable Parcel parcel) {
        List<String> errors = new ArrayList<>();

        if (parcel == null) {
            errors.add("Посылка не может быть null");
            return errors;
        }

        validateSameWidth(errors, parcel);
        validateConnected(errors, parcel);

        return errors;
    }

    /**
     * Проверяет, что все строки посылки имеют одинаковую ширину.
     *
     * @param errors список ошибок для пополнения (не может быть null)
     * @param parcel посылка для проверки (не может быть null)
     */
    private void validateSameWidth(List<String> errors, Parcel parcel) {
        boolean[][] grid = parcel.grid();
        if (grid.length == 0) {
            errors.add("Посылка не содержит grid");
            return;
        }

        int expectedWidth = grid[0].length;

        for (int i = 1; i < grid.length; i++) {
            if (grid[i].length != expectedWidth) {
                errors.add(
                        String.format("Строка %d имеет ширину %d, ожидалась %d", i + 1, grid[i].length, expectedWidth));
            }
        }
    }

    /**
     * Проверяет связность посылки (все заполненные клетки должны быть соединены).
     *
     * @param errors список ошибок для пополнения (не может быть null)
     * @param parcel посылка для проверки (не может быть null)
     */
    private void validateConnected(List<String> errors, Parcel parcel) {
        boolean[][] grid = parcel.grid();
        int height = parcel.height();
        int width = parcel.width();

        if (height == 0 || width == 0) {
            errors.add("Посылка имеет нулевой размер");
            return;
        }

        // Находим первую заполненную клетку
        int startY = -1;
        int startX = -1;
        for (int i = 0; i < height && startY == -1; i++) {
            for (int j = 0; j < width; j++) {
                if (grid[i][j]) {
                    startY = i;
                    startX = j;
                    break;
                }
            }
        }

        if (startY == -1) {
            errors.add("Посылка не содержит заполненных клеток");
            return;
        }

        // Подсчитываем общее количество заполненных клеток
        int totalCells = countFilledCells(grid);

        // Считаем количество клеток в основном компоненте
        int connectedCount = countConnectedCells(grid, startY, startX);

        if (connectedCount != totalCells) {
            errors.add(String.format(
                    "Посылка не является связной. Найдено %d клеток, но только %d из них связаны",
                    totalCells, connectedCount));
        }
    }

    /**
     * Подсчитывает количество заполненных клеток в посылке.
     *
     * @param grid сетка посылки (не может быть null)
     * @return количество заполненных клеток
     */
    private int countFilledCells(boolean[][] grid) {
        int count = 0;
        for (boolean[] row : grid) {
            for (boolean cell : row) {
                if (cell) count++;
            }
        }
        return count;
    }

    /**
     * Подсчитывает количество клеток в компоненте связности.
     *
     * @param grid   сетка посылки (не может быть null)
     * @param startY начальная координата Y
     * @param startX начальная координата X
     * @return количество клеток в компоненте
     */
    private int countConnectedCells(boolean[][] grid, int startY, int startX) {
        int height = grid.length;
        int width = grid[0].length;
        boolean[][] visited = new boolean[height][width];

        return dfs(grid, visited, startY, startX);
    }

    /**
     * Рекурсивный обход графа для поиска связных клеток.
     *
     * @param grid    сетка посылки (не может быть null)
     * @param visited массив посещённых клеток (не может быть null)
     * @param y       текущая координата Y
     * @param x       текущая координата X
     * @return количество посещённых клеток
     */
    private int dfs(boolean[][] grid, boolean[][] visited, int y, int x) {
        if (y < 0 || y >= grid.length || x < 0 || x >= grid[0].length) {
            return 0;
        }
        if (!grid[y][x] || visited[y][x]) {
            return 0;
        }

        visited[y][x] = true;
        int count = 1;

        count += dfs(grid, visited, y - 1, x);
        count += dfs(grid, visited, y + 1, x);
        count += dfs(grid, visited, y, x - 1);
        count += dfs(grid, visited, y, x + 1);

        return count;
    }
}
