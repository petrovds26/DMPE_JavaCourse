package ru.hofftech.core.service.loader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hofftech.core.model.core.Machine;
import ru.hofftech.core.model.core.Parcel;
import ru.hofftech.core.service.parcer.ParserParcelBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Поиск позиции для посылки")
class LoadStrategyPositionFinderTest {
    private LoadStrategyPositionFinder positionFinder;
    private ParserParcelBuilder parcelBuilder;

    @BeforeEach
    void setUp() {
        LoadStrategySupportChecker loadStrategySupportChecker = new LoadStrategySupportChecker();
        positionFinder = new LoadStrategyPositionFinder(loadStrategySupportChecker);
        parcelBuilder = new ParserParcelBuilder();
    }

    @Test
    @DisplayName("Должен найти позицию (0,0) для посылки 1x1 в пустой машине")
    void findBestPosition_EmptyMachine_SmallParcel_ReturnsOrigin() {
        // Arrange
        Machine machine = new Machine();
        Parcel parcel = parcelBuilder.buildFromLines("Name", List.of("1"));

        // Act
        int[] position = positionFinder.findBestPosition(machine, parcel);

        // Assert
        assertThat(position).isEqualTo(new int[] {0, 0});
    }

    @Test
    @DisplayName("Должен найти позицию справа от существующей посылки")
    void findBestPosition_OneParcelPlaced_FindsPositionToTheRight() {
        // Arrange
        Machine machine = new Machine();

        Parcel placedParcel = parcelBuilder.buildFromLines("Name", List.of("222", "222")); // 3x2

        machine = machine.placeParcel(placedParcel, 0, 0);

        Parcel newParcel = parcelBuilder.buildFromLines("Name", List.of("1")); // 1x1

        // Act
        int[] position = positionFinder.findBestPosition(machine, newParcel);

        // Assert
        assertThat(position).isEqualTo(new int[] {3, 0});
    }

    @Test
    @DisplayName("Должен найти позицию над существующей посылкой")
    void findBestPosition_OneParcelPlaced_FindsPositionAbove() {
        // Arrange
        Machine machine = new Machine();

        Parcel placedParcel = parcelBuilder.buildFromLines("Name", List.of("222", "222")); // 3x2

        machine = machine.placeParcel(placedParcel, 2, 0);

        Parcel newParcel = parcelBuilder.buildFromLines("Name", List.of("333")); // 3x1

        // Act
        int[] position = positionFinder.findBestPosition(machine, newParcel);

        // Assert
        assertThat(position).isEqualTo(new int[] {1, 2});
    }

    @Test
    @DisplayName("Должен отклонить позицию с недостаточной опорой")
    void findBestPosition_InsufficientSupport_ReturnsNull() {
        // Arrange
        Machine machine = new Machine();

        Parcel placedParcel = parcelBuilder.buildFromLines("Name", List.of("11")); // 1x1

        machine = machine.placeParcel(placedParcel, 2, 0);

        Parcel newParcel = parcelBuilder.buildFromLines("Name", List.of("33333")); // 5x1

        // Act
        int[] position = positionFinder.findBestPosition(machine, newParcel);

        // Assert
        assertThat(position).isNull(); // не должно найти место из-за недостатка опоры
    }
}
