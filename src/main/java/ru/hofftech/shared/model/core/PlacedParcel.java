package ru.hofftech.shared.model.core;

import lombok.Builder;

/**
 * Информация о размещённой в машине посылке.
 * Содержит саму посылку и её координаты левого нижнего угла.
 */
@Builder
public record PlacedParcel(
        Parcel parcel, // Сама посылка
        int x, // Координата X (от 0)
        int y // Координата Y (от 0)
        ) {
    /**
     * Возвращает максимальную X координату, занимаемую посылкой.
     *
     * @return максимальная X координата (x + ширина - 1)
     */
    public int getMaxX() {
        return x + parcel.getWidth() - 1;
    }

    /**
     * Возвращает максимальную Y координату, занимаемую посылкой.
     *
     * @return максимальная Y координата (y + высота - 1)
     */
    public int getMaxY() {
        return y + parcel.getHeight() - 1;
    }
}
