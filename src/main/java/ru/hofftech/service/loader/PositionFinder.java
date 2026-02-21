package ru.hofftech.service.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.model.entity.Machine;
import ru.hofftech.model.entity.Parcel;

/**
 * Ищет наилучшую позицию для размещения посылки
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord") // Стратегия содержит логику, не только данные
public class PositionFinder {

    private final SupportChecker supportChecker;

    public PositionFinder() {
        this.supportChecker = new SupportChecker();
    }

    /**
     * Находит наилучшую позицию для размещения посылки
     * @param machine машина
     * @param parcel посылка
     * @return массив [x, y] или null, если место не найдено
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
     * Проверяет, можно ли разместить посылку в указанной позиции
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
                    supportChecker.getSupportInfo(machine, parcel, x, y));
            return false;
        }

        return true;
    }
}
