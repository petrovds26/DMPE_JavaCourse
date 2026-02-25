package ru.hofftech.service.loader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hofftech.model.core.Machine;
import ru.hofftech.model.core.Parcel;
import ru.hofftech.service.parser.ParcelBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Проверка опоры для посылки")
class SupportCheckerTest {

    private SupportChecker supportChecker;
    private ParcelBuilder parcelBuilder;

    @BeforeEach
    void setUp() {
        supportChecker = new SupportChecker();
        parcelBuilder = new ParcelBuilder();
    }

    @Test
    @DisplayName("Должен вернуть true для посылки на полу (y=0)")
    void hasEnoughSupport_OnFloor_ReturnsTrue() {
        // Arrange
        Machine machine = new Machine();
        Parcel parcel = parcelBuilder.buildFromLines(List.of("333", "333")); // 3x2

        // Act
        boolean result = supportChecker.hasEnoughSupport(machine, parcel, 0, 0);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Должен вернуть true когда опора под всеми клетками нижнего ряда")
    void hasEnoughSupport_FullSupport_ReturnsTrue() {
        // Arrange
        Machine machine = new Machine();

        // Создаём опору под всей нижней частью
        Parcel supportParcel = parcelBuilder.buildFromLines(List.of("333")); // 3x1
        machine = machine.placeParcel(supportParcel, 1, 0);

        Parcel parcel = parcelBuilder.buildFromLines(List.of("333", "333")); // 3x2 - ставим сверху

        // Act
        boolean result = supportChecker.hasEnoughSupport(machine, parcel, 1, 1);

        // Assert
        assertThat(result).isTrue(); // 3 из 3 клеток опоры = 100% > 50%
    }

    @Test
    @DisplayName("Должен вернуть true когда опора ровно >50% (3 из 5)")
    void hasEnoughSupport_ExactlyEnoughSupport_ReturnsTrue() {
        // Arrange
        Machine machine = new Machine();

        // Создаём опору под 3 клетками из 5
        Parcel supportParcel1 = parcelBuilder.buildFromLines(List.of("1")); // 1x1
        Parcel supportParcel2 = parcelBuilder.buildFromLines(List.of("1")); // 1x1
        Parcel supportParcel3 = parcelBuilder.buildFromLines(List.of("1")); // 1x1

        machine = machine.placeParcel(supportParcel1, 0, 0);
        machine = machine.placeParcel(supportParcel2, 2, 0);
        machine = machine.placeParcel(supportParcel3, 4, 0);

        Parcel parcel = parcelBuilder.buildFromLines(List.of("55555")); // 5x1 - ставим сверху

        // Act
        boolean result = supportChecker.hasEnoughSupport(machine, parcel, 0, 1);

        // Assert
        assertThat(result).isTrue(); // 3 клетки опоры > 2.5 (50% от 5)
    }

    @Test
    @DisplayName("Должен вернуть false когда опора 50% или меньше (2 из 5)")
    void hasEnoughSupport_InsufficientSupport_ReturnsFalse() {
        // Arrange
        Machine machine = new Machine();

        // Создаём опору только под 2 клетками из 5
        Parcel supportParcel1 = parcelBuilder.buildFromLines(List.of("1")); // 1x1
        Parcel supportParcel2 = parcelBuilder.buildFromLines(List.of("1")); // 1x1

        machine = machine.placeParcel(supportParcel1, 0, 0);
        machine = machine.placeParcel(supportParcel2, 2, 0);

        Parcel parcel = parcelBuilder.buildFromLines(List.of("55555")); // 5x1 - ставим сверху

        // Act
        boolean result = supportChecker.hasEnoughSupport(machine, parcel, 0, 1);

        // Assert
        assertThat(result).isFalse(); // 2 клетки опоры <= 2.5 (50% от 5)
    }
}
