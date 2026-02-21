package ru.hofftech.service.loader.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.model.dto.LoadingResult;
import ru.hofftech.model.entity.Machine;
import ru.hofftech.model.entity.Parcel;
import ru.hofftech.service.loader.PositionFinder;
import ru.hofftech.service.loader.strategy.ParcelLoadingStrategy;
import ru.hofftech.service.loader.strategy.ParcelLoadingStrategyType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Стратегия плотной упаковки посылок
 * - Ищет самое низкое и левое место для каждой посылки
 * - Проверяет опору >50%
 * - Сортирует посылки по размеру (широкие сначала)
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord") // Стратегия содержит логику, не только данные
public class DensePackingStrategy implements ParcelLoadingStrategy {

    private final PositionFinder positionFinder;

    public DensePackingStrategy() {
        this.positionFinder = new PositionFinder();
    }

    @Override
    public ParcelLoadingStrategyType getAlgorithmType() {
        return ParcelLoadingStrategyType.DENSE_PACKING;
    }

    @Override
    public LoadingResult loadParcels(List<Parcel> parcels) {
        List<Machine> machines = new ArrayList<>();
        List<Parcel> oversizedParcels = new ArrayList<>();

        // Шаг 1: Сортируем посылки по убыванию ширины (самые широкие сначала)
        List<Parcel> sortedParcels = sortParcelsByWidth(parcels);

        // Шаг 2: Обрабатываем каждую посылку
        for (Parcel parcel : sortedParcels) {
            log.debug("Упаковка посылки {}x{} с символом '{}'", parcel.getWidth(), parcel.getHeight(), parcel.symbol());

            // Проверяем, влезает ли посылка в машину по размерам
            if (!fitsInMachine(parcel)) {
                log.warn("Посылка {}x{} слишком велика для машины", parcel.getWidth(), parcel.getHeight());
                oversizedParcels.add(parcel);
                continue;
            }

            // Пытаемся разместить в существующих машинах
            boolean placed = tryPlaceInExistingMachines(machines, parcel);

            if (!placed) {
                // Не влезло ни в одну существующую - создаём новую машину
                log.debug("Посылка не влезла ни в одну существующую машину, создаём новую");
                Machine newMachine = new Machine();
                newMachine = newMachine.placeParcel(parcel, 0, 0);
                machines.add(newMachine);
                log.debug("Посылка размещена в новой машине в позиции (0,0)");
            }
        }

        LoadingResult result = LoadingResult.builder()
                .machines(machines)
                .oversizedParcels(oversizedParcels)
                .build();

        log.debug(
                "Упаковано {} посылок в {} машин. Отложено {} посылок",
                result.getTotalParcelsProcessed(),
                machines.size(),
                oversizedParcels.size());

        return result;
    }

    @Override
    public String getAlgorithmName() {
        return "Плотная упаковка";
    }

    /**
     * Пытается разместить посылку в одной из существующих машин
     * @return true если посылка размещена
     */
    private boolean tryPlaceInExistingMachines(List<Machine> machines, Parcel parcel) {
        for (int i = 0; i < machines.size(); i++) {
            Machine machine = machines.get(i);

            int[] position = positionFinder.findBestPosition(machine, parcel);

            if (position != null) {
                // Размещаем в этой машине
                Machine updatedMachine = machine.placeParcel(parcel, position[0], position[1]);
                machines.set(machines.indexOf(machine), updatedMachine);

                log.debug(
                        "Посылка размещена в существующей машине #{} в позиции ({},{})",
                        i + 1,
                        position[0],
                        position[1]);
                return true;
            }
        }

        return false;
    }

    /**
     * Сортирует посылки по убыванию ширины (самые широкие сначала)
     */
    private List<Parcel> sortParcelsByWidth(List<Parcel> parcels) {
        return parcels.stream()
                .sorted(Comparator.comparingInt(Parcel::getWidth).reversed())
                .toList();
    }

    /**
     * Проверяет, влезает ли посылка в машину по габаритам
     */
    private boolean fitsInMachine(Parcel parcel) {
        return parcel.getWidth() <= Machine.DEFAULT_WIDTH && parcel.getHeight() <= Machine.DEFAULT_HEIGHT;
    }
}
