package ru.hofftech.core.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.core.model.core.Machine;
import ru.hofftech.core.model.core.Parcel;

/**
 * Утилита для получения отладочной информации о проверке опоры.
 * <p>
 * Используется при поиске оптимальной позиции для размещения посылки.
 */
@NullMarked
@UtilityClass
public class SupportUtil {

    /**
     * Возвращает информацию об опоре для отладки.
     *
     * @param machine машина, в которую размещается посылка
     * @param parcel  размещаемая посылка
     * @param x       координата X левого нижнего угла
     * @param y       координата Y левого нижнего угла
     * @return строка с информацией об опоре
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
