package ru.hofftech.shared.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.PlacedParcel;

/**
 * Утилита для форматированного вывода посылок.
 */
@UtilityClass
public class PrintStringUtil {

    /**
     * Форматирует информацию о посылке по единому шаблону.
     *
     * @param parcel посылка (не может быть null)
     * @param placed информация о размещении (может быть null)
     * @return отформатированная строка (не может быть null)
     */
    @NonNull
    public static String parcelRender(@NonNull Parcel parcel, @Nullable PlacedParcel placed) {
        StringBuilder sb = new StringBuilder();

        // Заголовок с символом и габаритами
        sb.append(String.format("Посылка '%s' [%dx%d]", parcel.name(), parcel.getWidth(), parcel.getHeight()))
                .append("\n");
        sb.append(String.format("Символ: '%c'", parcel.getSymbol())).append("\n");

        // Позиция, если известна
        if (placed != null) {
            sb.append(String.format(
                            "Позиция (%d,%d)-(%d,%d)", placed.x(), placed.y(), placed.getMaxX(), placed.getMaxY()))
                    .append("\n");
        }

        // Рендер посылки
        sb.append(parcel.getForm()).append("\n");

        // Разделитель
        sb.append("-".repeat(30)).append("\n");

        return sb.toString();
    }

    /**
     * Форматирует информацию о посылке без координат.
     *
     * @param parcel посылка (не может быть null)
     * @return отформатированная строка (не может быть null)
     */
    @NonNull
    public static String parcelRender(@NonNull Parcel parcel) {
        return parcelRender(parcel, null);
    }
}
