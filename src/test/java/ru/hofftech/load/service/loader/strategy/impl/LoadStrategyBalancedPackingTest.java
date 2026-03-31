package ru.hofftech.load.service.loader.strategy.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.load.model.enums.LoadStrategyType;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.PlacedParcel;
import ru.hofftech.shared.service.parser.ParserParcelBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Стратегия упаковки посылок: Равномерная погрузка")
class LoadStrategyBalancedPackingTest {
    private LoadStrategyBalancedPacking strategy;
    private ParserParcelBuilder parcelBuilder;

    @BeforeEach
    void setUp() {
        strategy = new LoadStrategyBalancedPacking();
        parcelBuilder = new ParserParcelBuilder();
    }

    @Test
    @DisplayName("Должен обработать посылку максимального размера 6x6")
    void loadParcels_MaxSizeParcel_PlacesSuccessfully() {
        // Arrange
        List<String> parcelLines = List.of("666666", "666666", "666666", "666666", "666666", "666666");
        Parcel parcel = parcelBuilder.buildFromLines("name", parcelLines);
        List<Parcel> parcels = List.of(parcel);

        Machine inputMachine = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.loadStrategyParcelInvalids()).isEmpty();
        assertThat(result.machines()).hasSize(1);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(1);

        Machine machine = result.machines().getFirst();
        assertThat(machine.parcels()).hasSize(1);
        assertThat(machine.parcels().getFirst().parcel()).isEqualTo(parcel);
        assertThat(machine.parcels().getFirst().x()).isZero();
        assertThat(machine.parcels().getFirst().y()).isZero();
    }

    @Test
    @DisplayName("Должен равномерно распределить три разные посылки 1х3, 2х3, 3x3 по двум машинам")
    void loadParcels_ThreeDifferentValidParcels_DistributesEvenly() {
        // Arrange
        Parcel parcel1 = parcelBuilder.buildFromLines("name", List.of("1", "1", "1")); // 1x3
        Parcel parcel2 = parcelBuilder.buildFromLines("name", List.of("22", "22", "22")); // 2x3
        Parcel parcel3 = parcelBuilder.buildFromLines("name", List.of("333", "333", "333")); // 3x3

        List<Parcel> parcels = List.of(parcel1, parcel2, parcel3);

        Machine machine1 = new Machine(6, 6);
        Machine machine2 = new Machine(6, 6);
        List<Machine> machines = List.of(machine1, machine2);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.loadStrategyParcelInvalids()).isEmpty();
        assertThat(result.machines()).hasSize(2);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(3);

        // Проверяем, что посылки распределены по машинам
        Machine resultMachine1 = result.machines().get(0);
        Machine resultMachine2 = result.machines().get(1);

        // Каждая машина должна содержать хотя бы одну посылку
        assertThat(resultMachine1.parcels()).isNotEmpty();
        assertThat(resultMachine2.parcels()).isNotEmpty();
    }

    @Test
    @DisplayName("Должен упаковать три разные посылки 1х3, 7х3, 3x3 в 2 машины и 1 oversized")
    void loadParcels_TwoValidAndOneOversizedParcels_ReturnsTwoMachinesAndOneOversized() {
        // Arrange
        Parcel validParcel1 = parcelBuilder.buildFromLines("name", List.of("1", "1", "1")); // 1x3

        // Oversized - ширина 7 > 6
        Parcel oversizedParcel = parcelBuilder.buildFromLines("name", List.of("7777777", "7777777", "7777777"));

        Parcel validParcel2 = parcelBuilder.buildFromLines("name", List.of("333", "333", "333")); // 3x3

        List<Parcel> parcels = List.of(validParcel1, oversizedParcel, validParcel2);

        Machine machine1 = new Machine(6, 6);
        Machine machine2 = new Machine(6, 6);
        List<Machine> machines = List.of(machine1, machine2);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();

        // Проверяем машины - валидные должны распределиться по двум машинам
        assertThat(result.machines()).hasSize(2);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(2);

        // Проверяем oversized
        assertThat(result.loadStrategyParcelInvalids()).hasSize(1);
        assertThat(result.loadStrategyParcelInvalids().getFirst().parcel()).isEqualTo(oversizedParcel);
    }

    @Test
    @DisplayName("Должен вернуть пустой результат при пустом списке посылок")
    void loadParcels_EmptyList_ReturnsEmptyResult() {
        // Arrange
        List<Parcel> parcels = List.of();

        Machine inputMachine = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.loadStrategyParcelInvalids()).isEmpty();
        assertThat(result.machines()).hasSize(1); // Машины остаются пустыми
        assertThat(result.getTotalParcelsProcessed()).isZero();
    }

    @Test
    @DisplayName("Должен правильно сортировать посылки по ширине (широкие сначала)")
    void loadParcels_UnsortedParcels_SortsByWidthBeforePlacing() {
        // Arrange
        Parcel narrowParcel = parcelBuilder.buildFromLines("name", List.of("1")); // 1x1
        Parcel wideParcel = parcelBuilder.buildFromLines("name", List.of("55555")); // 5x1
        Parcel mediumParcel = parcelBuilder.buildFromLines("name", List.of("333")); // 3x1

        // Передаём в неправильном порядке
        List<Parcel> parcels = List.of(narrowParcel, mediumParcel, wideParcel);

        Machine machine1 = new Machine(6, 6);
        Machine machine2 = new Machine(6, 6);
        List<Machine> machines = List.of(machine1, machine2);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.loadStrategyParcelInvalids()).isEmpty();
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(3);

        // Широкая посылка должна быть размещена первой (в машине 1)
        Assertions.assertNotNull(result.machines());
        Machine resultMachine = result.machines().getFirst();
        var placedParcels = resultMachine.parcels();

        var widePlaced =
                placedParcels.stream().filter(p -> p.parcel().getWidth() == 5).findFirst();

        assertThat(widePlaced).isPresent();
    }

    @Test
    @DisplayName("Должен вернуть только одну успешную посылку, когда только одна машина и одна посылка помещается")
    void loadParcels_TwoParcelsOneFits_ReturnsOneMachineAndOneInvalid() {
        // Arrange
        Parcel parcel1 = parcelBuilder.buildFromLines("name", List.of("11111", "11111", "11111", "11111")); // 5x4
        Parcel parcel2 = parcelBuilder.buildFromLines("name", List.of("222222", "222222", "222222")); // 6x3

        List<Parcel> parcels = List.of(parcel1, parcel2);

        Machine inputMachine = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();

        // Одна машина с одной посылкой
        assertThat(result.machines()).hasSize(1);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(1);
        assertThat(result.loadStrategyParcelInvalids()).hasSize(1);
    }

    @Test
    @DisplayName("Должен равномерно распределить посылки при наличии нескольких машин")
    void loadParcels_MultipleParcelsMultipleMachines_DistributesEvenly() {
        // Arrange
        Parcel parcel1 = parcelBuilder.buildFromLines("name", List.of("111")); // 3x1
        Parcel parcel2 = parcelBuilder.buildFromLines("name", List.of("222")); // 3x1
        Parcel parcel3 = parcelBuilder.buildFromLines("name", List.of("333")); // 3x1
        Parcel parcel4 = parcelBuilder.buildFromLines("name", List.of("444")); // 3x1
        Parcel parcel5 = parcelBuilder.buildFromLines("name", List.of("555")); // 3x1
        Parcel parcel6 = parcelBuilder.buildFromLines("name", List.of("666")); // 3x1

        List<Parcel> parcels = List.of(parcel1, parcel2, parcel3, parcel4, parcel5, parcel6);

        Machine machine1 = new Machine(6, 6);
        Machine machine2 = new Machine(6, 6);
        Machine machine3 = new Machine(6, 6);
        List<Machine> machines = List.of(machine1, machine2, machine3);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.loadStrategyParcelInvalids()).isEmpty();
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(6);

        // Проверяем равномерное распределение (по 2 посылки в каждой машине)
        Assertions.assertNotNull(result.machines());
        for (Machine machine : result.machines()) {
            assertThat(machine.parcels()).hasSize(2);
        }
    }

    @Test
    @DisplayName("Должен вернуть правильный тип алгоритма")
    void getAlgorithmType_ReturnsCorrectType() {
        // Act & Assert
        assertThat(strategy.getAlgorithmType()).isEqualTo(LoadStrategyType.BALANCED_PACKING);
        assertThat(strategy.getAlgorithmName()).isEqualTo("Равномерная погрузка");
    }

    @Test
    @DisplayName("Должен корректно обрабатывать посылки с дырками")
    void loadParcels_ParcelWithHoles_PlacesCorrectly() {
        // Arrange
        Parcel parcel = parcelBuilder.buildFromLines("name", List.of("999", "9 9", "999")); // 3x3 с дыркой

        List<Parcel> parcels = List.of(parcel);

        Machine machine = new Machine(6, 6);
        List<Machine> machines = List.of(machine);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.loadStrategyParcelInvalids()).isEmpty();
        assertThat(result.machines()).hasSize(1);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(1);

        Machine resultMachine = result.machines().getFirst();
        PlacedParcel placed = resultMachine.parcels().getFirst();

        // Проверяем, что форма посылки сохранилась
        assertThat(placed.parcel().getForm()).isEqualTo("999\n9 9\n999");
    }
}
