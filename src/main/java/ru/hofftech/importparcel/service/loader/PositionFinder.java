package ru.hofftech.importparcel.service.loader;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.util.SupportUtil;

/**
 * Класс для поиска оптимальной позиции размещения посылки в машине.
 * Реализует стратегию поиска "снизу вверх, слева направо".
 */
@Slf4j
public class PositionFinder {

    private final SupportChecker supportChecker;

    /**
     * Конструктор, инициализирующий проверку опоры.
     */
    public PositionFinder() {
        this.supportChecker = new SupportChecker();
    }

    /**
     * Находит наилучшую позицию для размещения посылки в машине.
     * Поиск выполняется снизу вверх и слева направо.
     * Возвращается самая нижняя и левая подходящая позиция.
     *
     * @param machine машина, в которую размещается посылка
     * @param parcel  посылка для размещения
     * @return массив [x, y] с координатами или null, если место не найдено
     */
    public int[] findBestPosition(Machine machine, Parcel parcel) {
        int[] bestPosition = null;

        int maxY = Machine.DEFAULT_HEIGHT - parcel.getHeight();
        int maxX = Machine.DEFAULT_WIDTH - parcel.getWidth();

        log.debug("Поиск позиции для посылки {}x{}", parcel.getWidth(), parcel.getHeight());

        // Ищем снизу вверх, слева направо
        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                if (canPlaceAt(machine, parcel, x, y)) {
                    log.trace("Найдена позиция ({},{})", x, y);
                    // Самая нижняя и левая позиция - оптимальна
                    if (bestPosition == null || y < bestPosition[1] || (y == bestPosition[1] && x < bestPosition[0])) {
                        bestPosition = new int[] {x, y};
                    }
                }
            }
        }

        if (bestPosition != null) {
            log.debug("Лучшая позиция: ({},{})", bestPosition[0], bestPosition[1]);
        } else {
            log.debug("Место не найдено");
        }

        return bestPosition;
    }

    /**
     * Проверяет возможность размещения посылки в указанной позиции.
     * Учитывается пересечение с другими посылками и достаточность опоры.
     *
     * @param machine машина
     * @param parcel  посылка
     * @param x       координата X
     * @param y       координата Y
     * @return true если размещение возможно
     */
    private boolean canPlaceAt(Machine machine, Parcel parcel, int x, int y) {
        // Проверяем, не пересекается ли с другими посылками
        if (machine.isPlaceOccupied(parcel, x, y)) {
            return false;
        }

        // Проверяем достаточность опоры
        if (!supportChecker.hasEnoughSupport(machine, parcel, x, y)) {
            log.trace(
                    "Позиция ({},{}) отклонена - недостаточно опоры: {}",
                    x,
                    y,
                    SupportUtil.getSupportInfo(machine, parcel, x, y));
            return false;
        }

        return true;
    }
}
