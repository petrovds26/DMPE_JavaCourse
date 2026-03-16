package ru.hofftech.load.service.loader.strategy.impl;

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

@DisplayName("Стратегия упаковки посылок: Одна посылка - одна машина")
class LoadStrategyOneParcelPerMachineTest {
    private LoadStrategyOneParcelPerMachine strategy;
    private ParserParcelBuilder parcelBuilder;

    @BeforeEach
    void setUp() {
        strategy = new LoadStrategyOneParcelPerMachine();
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
    @DisplayName("Должен упаковать три разные посылки 1х3, 2х3, 3x3 в три отдельные машины")
    void loadParcels_ThreeDifferentValidParcels_ReturnsThreeMachines() {
        // Arrange
        Parcel parcel1 = parcelBuilder.buildFromLines("name", List.of("1", "1", "1"));

        Parcel parcel2 = parcelBuilder.buildFromLines("name", List.of("22", "22", "22"));

        Parcel parcel3 = parcelBuilder.buildFromLines("name", List.of("333", "333", "333"));

        List<Parcel> parcels = List.of(parcel1, parcel2, parcel3);

        Machine inputMachine = new Machine(6, 6);
        Machine inputMachine2 = new Machine(6, 6);
        Machine inputMachine3 = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine, inputMachine2, inputMachine3);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.loadStrategyParcelInvalids()).isEmpty();
        assertThat(result.machines()).hasSize(3);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(3);

        // Проверяем, что каждая посылка в отдельной машине
        for (int i = 0; i < 3; i++) {
            Machine machine = result.machines().get(i);
            assertThat(machine.parcels()).hasSize(1);
            assertThat(machine.parcels().getFirst().parcel()).isEqualTo(parcels.get(i));
        }
    }

    @Test
    @DisplayName("Должен упаковать три разные посылки 1х3, 7х3, 3x3 в 2 отдельные машины")
    void loadParcels_TwoValidAndOneOversizedParcels_ReturnsTwoMachinesAndOneOversized() {
        // Arrange
        Parcel validParcel1 = parcelBuilder.buildFromLines("name", List.of("1", "1", "1"));

        // Oversized - ширина 7 > 6
        Parcel oversizedParcel = parcelBuilder.buildFromLines("name", List.of("7777777", "7777777", "7777777"));

        Parcel validParcel2 = parcelBuilder.buildFromLines("name", List.of("333", "333", "333"));

        List<Parcel> parcels = List.of(validParcel1, oversizedParcel, validParcel2);

        Machine inputMachine = new Machine(6, 6);
        Machine inputMachine2 = new Machine(6, 6);
        Machine inputMachine3 = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine, inputMachine2, inputMachine3);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();

        // Проверяем машины. Из должно быть три, но заполнены только две
        assertThat(result.machines()).hasSize(3);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(2);

        // Проверяем oversized
        assertThat(result.loadStrategyParcelInvalids()).hasSize(1);
        assertThat(result.loadStrategyParcelInvalids().getFirst().parcel()).isEqualTo(oversizedParcel);

        // Проверяем, что в машинах только валидные посылки
        var placedParcels = result.machines().stream()
                .flatMap(m -> m.parcels().stream())
                .map(PlacedParcel::parcel)
                .toList();
        assertThat(placedParcels).containsExactlyInAnyOrder(validParcel1, validParcel2);
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
        assertThat(result.getTotalParcelsProcessed()).isZero();
    }

    @Test
    @DisplayName("Должен вернуть только одну успешную посылку, так как машин не хватает")
    void loadParcels_TwoParcelsOneDoesNotFit_ReturnsOneMachineAndOneInvalid() {
        // Arrange
        Parcel parcel1 = parcelBuilder.buildFromLines("name", List.of("1", "1", "1"));
        Parcel parcel2 = parcelBuilder.buildFromLines("name", List.of("22", "22", "22"));
        Parcel parcel3 = parcelBuilder.buildFromLines("name", List.of("333", "333", "333"));

        List<Parcel> parcels = List.of(parcel1, parcel2, parcel3);

        Machine inputMachine = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine);

        // Act
        LoadResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();

        // Должны получить 1 заполненную машину
        assertThat(result.machines()).hasSize(1);
        assertThat(result.loadStrategyParcelInvalids()).hasSize(2);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(1);
    }

    @Test
    @DisplayName("Должен вернуть правильный тип алгоритма")
    void getAlgorithmType_ReturnsCorrectType() {
        // Act & Assert
        assertThat(strategy.getAlgorithmType()).isEqualTo(LoadStrategyType.ONE_PARCEL_PER_MACHINE);
        assertThat(strategy.getAlgorithmName()).isEqualTo("Одна посылка на машину");
    }
}
