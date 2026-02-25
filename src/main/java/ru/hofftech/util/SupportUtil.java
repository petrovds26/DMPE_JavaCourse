package ru.hofftech.util;

import lombok.experimental.UtilityClass;
import ru.hofftech.model.core.Machine;
import ru.hofftech.model.core.Parcel;

/**
 * Утилита для получения отладочной информации о проверке опоры
 */
@UtilityClass
public class SupportUtil {

    /**
     * Возвращает информацию об опоре для отладки
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
