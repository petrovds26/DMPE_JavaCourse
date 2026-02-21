package ru.hofftech.service.loader;

import ru.hofftech.model.entity.Machine;
import ru.hofftech.model.entity.Parcel;

/**
 * Проверяет достаточность опоры для посылки
 */
public class SupportChecker {

    /**
     * Проверяет, достаточно ли опоры под посылкой в указанной позиции
     * @param machine машина, в которую размещаем
     * @param parcel посылка
     * @param x координата X левого нижнего угла посылки
     * @param y координата Y левого нижнего угла посылки
     * @return true если опоры достаточно (>50%)
     */
    public boolean hasEnoughSupport(Machine machine, Parcel parcel, int x, int y) {
        // Если посылка на полу - всегда достаточно опоры
        if (y == 0) {
            return true;
        }

        int width = parcel.getWidth();
        boolean[][] parcelGrid = parcel.grid();

        // Находим все клетки нижнего ряда посылки, которые реально заполнены
        int totalBottomCells = 0;
        int supportedCells = 0;

        for (int i = 0; i < width; i++) {
            // Проверяем, есть ли клетка в нижнем ряду посылки
            if (parcelGrid[parcel.getHeight() - 1][i]) {
                totalBottomCells++;
                // Проверяем, есть ли опора под этой клеткой
                if (machine.grid()[y - 1][x + i] != ' ') {
                    supportedCells++;
                }
            }
        }

        // Рассчитываем необходимый минимум (>50%)
        int neededSupport = (totalBottomCells / 2) + 1;

        return supportedCells >= neededSupport;
    }

    /**
     * Возвращает количество клеток опоры для отладки
     */
    public String getSupportInfo(Machine machine, Parcel parcel, int x, int y) {
        if (y == 0) {
            return "на полу";
        }

        int width = parcel.getWidth();
        boolean[][] parcelGrid = parcel.grid();

        int totalBottomCells = 0;
        int supportedCells = 0;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < width; i++) {
            if (parcelGrid[parcel.getHeight() - 1][i]) {
                totalBottomCells++;
                boolean hasSupport = machine.grid()[y - 1][x + i] != ' ';
                if (hasSupport) {
                    supportedCells++;
                }
                sb.append(String.format(" [%d:%s]", i, hasSupport ? "есть" : "нет"));
            }
        }

        int needed = (totalBottomCells / 2) + 1;
        return String.format("опора: %d/%d (нужно %d)%s", supportedCells, totalBottomCells, needed, sb);
    }
}
