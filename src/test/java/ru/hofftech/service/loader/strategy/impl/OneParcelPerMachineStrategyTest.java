package ru.hofftech.service.loader.strategy.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hofftech.model.core.Machine;
import ru.hofftech.model.core.Parcel;
import ru.hofftech.model.core.PlacedParcel;
import ru.hofftech.model.dto.LoadingResult;
import ru.hofftech.service.loader.strategy.ParcelLoadingStrategyType;
import ru.hofftech.service.parser.ParcelBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Стратегия упаковки посылок: Одна посылка - одна машина")
class OneParcelPerMachineStrategyTest {

    private OneParcelPerMachineStrategy strategy;
    private ParcelBuilder parcelBuilder;

    @BeforeEach
    void setUp() {
        strategy = new OneParcelPerMachineStrategy();
        parcelBuilder = new ParcelBuilder();
    }

    @Test
    @DisplayName("Должен обработать посылку максимального размера 6x6")
    void loadParcels_MaxSizeParcel_PlacesSuccessfully() {
        // Arrange
        List<String> parcelLines = List.of("666666", "666666", "666666", "666666", "666666", "666666");
        Parcel parcel = parcelBuilder.buildFromLines(parcelLines);
        List<Parcel> parcels = List.of(parcel);

        // Act
        LoadingResult result = strategy.loadParcels(parcels);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.oversizedParcels()).isEmpty();
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
        Parcel parcel1 = parcelBuilder.buildFromLines(List.of("1", "1", "1"));

        Parcel parcel2 = parcelBuilder.buildFromLines(List.of("22", "22", "22"));

        Parcel parcel3 = parcelBuilder.buildFromLines(List.of("333", "333", "333"));

        List<Parcel> parcels = List.of(parcel1, parcel2, parcel3);

        // Act
        LoadingResult result = strategy.loadParcels(parcels);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.oversizedParcels()).isEmpty();
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
        Parcel validParcel1 = parcelBuilder.buildFromLines(List.of("1", "1", "1"));

        // Oversized - ширина 7 > 6
        Parcel oversizedParcel = parcelBuilder.buildFromLines(List.of("7777777", "7777777", "7777777"));

        Parcel validParcel2 = parcelBuilder.buildFromLines(List.of("333", "333", "333"));

        List<Parcel> parcels = List.of(validParcel1, oversizedParcel, validParcel2);

        // Act
        LoadingResult result = strategy.loadParcels(parcels);

        // Assert
        assertThat(result).isNotNull();

        // Проверяем машины
        assertThat(result.machines()).hasSize(2);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(2);

        // Проверяем oversized
        assertThat(result.oversizedParcels()).hasSize(1);
        assertThat(result.oversizedParcels().getFirst()).isEqualTo(oversizedParcel);

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

        // Act
        LoadingResult result = strategy.loadParcels(parcels);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.machines()).isEmpty();
        assertThat(result.oversizedParcels()).isEmpty();
        assertThat(result.getTotalParcelsProcessed()).isZero();
    }

    @Test
    @DisplayName("Должен вернуть правильный тип алгоритма")
    void getAlgorithmType_ReturnsCorrectType() {
        // Act & Assert
        assertThat(strategy.getAlgorithmType()).isEqualTo(ParcelLoadingStrategyType.ONE_PARCEL_PER_MACHINE);
        assertThat(strategy.getAlgorithmName()).isEqualTo("Одна посылка на машину");
    }
}
