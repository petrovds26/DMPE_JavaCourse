package ru.hofftech.core.service.loader;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import ru.hofftech.core.model.core.Machine;
import ru.hofftech.core.model.core.Parcel;

/**
 * Класс для проверки достаточности опоры под посылкой.
 * Требование: опора должна составлять более 50% площади основания посылки.
 */
@NullMarked
@Component
@RequiredArgsConstructor
public class LoadStrategySupportChecker {

    /**
     * Проверяет, достаточно ли опоры под посылкой в указанной позиции.
     * Для посылки на полу (y = 0) всегда возвращает true.
     *
     * @param machine машина, в которую размещаем (не может быть null)
     * @param parcel  посылка (не может быть null)
     * @param x       координата X левого нижнего угла посылки
     * @param y       координата Y левого нижнего угла посылки
     * @return true если опоры достаточно (>50% клеток нижнего ряда имеют опору)
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
