package ru.hofftech.service.loader;

import ru.hofftech.model.core.Machine;
import ru.hofftech.model.core.Parcel;

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
}
