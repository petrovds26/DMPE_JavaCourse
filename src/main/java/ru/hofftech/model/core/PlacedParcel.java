package ru.hofftech.model.core;

import lombok.Builder;

/**
 * Информация о размещённой в машине посылке
 */
@Builder
public record PlacedParcel(
        Parcel parcel, // Сама посылка
        int x, // Координата X (от 0)
        int y // Координата Y (от 0)
        ) {
    /**
     * @return максимальная X координата, занимаемая посылкой
     */
    public int getMaxX() {
        return x + parcel.getWidth() - 1;
    }

    /**
     * @return максимальная Y координата, занимаемая посылкой
     */
    public int getMaxY() {
        return y + parcel.getHeight() - 1;
    }
}
