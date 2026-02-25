package ru.hofftech.service.validation.impl;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.model.core.Parcel;
import ru.hofftech.service.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Валидатор для готовой Parcel
 * Проверяет:
 * - Наличие символа
 * - Одинаковую ширину строк
 * - Связность посылки
 */
@Slf4j
public class ParcelGridValidator implements Validator<Parcel> {

    @Override
    public List<String> validate(Parcel parcel) {
        List<String> errors = new ArrayList<>();

        if (parcel == null) {
            errors.add("Посылка не может быть null");
            return errors;
        }

        validateSymbol(errors, parcel);
        validateSameWidth(errors, parcel);
        validateConnected(errors, parcel);

        return errors;
    }

    private void validateSymbol(List<String> errors, Parcel parcel) {
        if (parcel.symbol() == null) {
            errors.add("Посылка не содержит символа");
        }
    }

    private void validateSameWidth(List<String> errors, Parcel parcel) {
        boolean[][] grid = parcel.grid();
        if (grid == null || grid.length == 0) {
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

    private int countFilledCells(boolean[][] grid) {
        int count = 0;
        for (boolean[] row : grid) {
            for (boolean cell : row) {
                if (cell) count++;
            }
        }
        return count;
    }

    private int countConnectedCells(boolean[][] grid, int startY, int startX) {
        int height = grid.length;
        int width = grid[0].length;
        boolean[][] visited = new boolean[height][width];

        return dfs(grid, visited, startY, startX);
    }

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
