package ru.hofftech.importparcel.service.loader.strategy.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.service.loader.strategy.ParcelLoadingStrategyType;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.PlacedParcel;
import ru.hofftech.shared.service.parser.ParcelBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Стратегия упаковки посылок: Плотная упаковка")
class DensePackingStrategyTest {

    private DensePackingStrategy strategy;
    private ParcelBuilder parcelBuilder;

    @BeforeEach
    void setUp() {
        strategy = new DensePackingStrategy();
        parcelBuilder = new ParcelBuilder();
    }

    @Test
    @DisplayName("Должен обработать посылку максимального размера 6x6")
    void loadParcels_MaxSizeParcel_PlacesSuccessfully() {
        // Arrange
        List<String> parcelLines = List.of("666666", "666666", "666666", "666666", "666666", "666666");
        Parcel parcel = parcelBuilder.buildFromLines(parcelLines);
        List<Parcel> parcels = List.of(parcel);

        Machine inputMachine = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine);

        // Act
        ImportParcelResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.importParcelInvalids()).isEmpty();
        assertThat(result.machines()).hasSize(1);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(1);

        Machine machine = result.machines().getFirst();
        assertThat(machine.parcels()).hasSize(1);
        assertThat(machine.parcels().getFirst().parcel()).isEqualTo(parcel);
        assertThat(machine.parcels().getFirst().x()).isZero();
        assertThat(machine.parcels().getFirst().y()).isZero();
    }

    @Test
    @DisplayName("Должен упаковать три разные посылки 1х3, 2х3, 3x3 в одну машину (плотная упаковка)")
    void loadParcels_ThreeDifferentValidParcels_PacksInOneMachine() {
        // Arrange
        Parcel parcel1 = parcelBuilder.buildFromLines(List.of("1", "1", "1")); // 1x3

        Parcel parcel2 = parcelBuilder.buildFromLines(List.of("22", "22", "22")); // 2x3

        Parcel parcel3 = parcelBuilder.buildFromLines(List.of("333", "333", "333")); // 3x3

        List<Parcel> parcels = List.of(parcel1, parcel2, parcel3);

        Machine inputMachine = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine);

        // Act
        ImportParcelResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.importParcelInvalids()).isEmpty();
        assertThat(result.machines()).hasSize(1);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(3);

        Machine machine = result.machines().getFirst();
        assertThat(machine.parcels()).hasSize(3);

        // Проверяем, что широкая посылка 3x3 размещена первой (сортировка по ширине)
        var placedParcels = machine.parcels();

        // Находим посылку 3x3 (она должна быть самой широкой)
        var wideParcel = placedParcels.stream()
                .filter(p -> p.parcel().getWidth() == 3)
                .findFirst()
                .orElseThrow();
        assertThat(wideParcel.x()).isZero();
        assertThat(wideParcel.y()).isZero();
    }

    @Test
    @DisplayName("Должен упаковать три разные посылки 1х3, 7х3, 3x3 в 1 машину и 1 oversized")
    void loadParcels_TwoValidAndOneOversizedParcels_ReturnsOneMachineAndOneOversized() {
        // Arrange
        Parcel validParcel1 = parcelBuilder.buildFromLines(List.of("1", "1", "1")); // 1x3

        // Oversized - ширина 7 > 6
        Parcel oversizedParcel = parcelBuilder.buildFromLines(List.of("7777777", "7777777", "7777777"));

        Parcel validParcel2 = parcelBuilder.buildFromLines(List.of("333", "333", "333")); // 3x3

        List<Parcel> parcels = List.of(validParcel1, oversizedParcel, validParcel2);

        Machine inputMachine = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine);

        // Act
        ImportParcelResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();

        // Проверяем машины - обе валидные должны поместиться в одну машину
        assertThat(result.machines()).hasSize(1);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(2);

        // Проверяем oversized
        assertThat(result.importParcelInvalids()).hasSize(1);
        assertThat(result.importParcelInvalids().getFirst().parcel()).isEqualTo(oversizedParcel);

        // Проверяем, что в машине обе валидные посылки
        Machine machine = result.machines().getFirst();
        assertThat(machine.parcels()).hasSize(2);

        var placedParcels = machine.parcels().stream().map(PlacedParcel::parcel).toList();
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
        ImportParcelResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.importParcelInvalids()).isEmpty();
        assertThat(result.getTotalParcelsProcessed()).isZero();
    }

    @Test
    @DisplayName("Должен создать несколько машин, если посылки не влезают в одну")
    void loadParcels_ParcelsThatDontFitInOneMachine_CreatesMultipleMachines() {
        // Arrange
        Parcel parcel1 = parcelBuilder.buildFromLines(List.of("11111", "11111", "11111", "11111")); // 5x4

        Parcel parcel2 = parcelBuilder.buildFromLines(List.of("222222", "222222", "222222")); // 6x3

        List<Parcel> parcels = List.of(parcel1, parcel2);

        Machine inputMachine = new Machine(6, 6);
        Machine inputMachine2 = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine, inputMachine2);

        // Act
        ImportParcelResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.importParcelInvalids()).isEmpty();

        // Должны получить две заполненные машины
        assertThat(result.machines()).hasSize(2);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(2);
    }

    @Test
    @DisplayName("Должен правильно сортировать посылки по ширине (широкие сначала)")
    void loadParcels_UnsortedParcels_SortsByWidthBeforePlacing() {
        // Arrange
        Parcel narrowParcel = parcelBuilder.buildFromLines(List.of("1")); // 1x1

        Parcel wideParcel = parcelBuilder.buildFromLines(List.of("55555")); // 5x1

        Parcel mediumParcel = parcelBuilder.buildFromLines(List.of("333")); // 3x1

        // Передаём в неправильном порядке
        List<Parcel> parcels = List.of(narrowParcel, mediumParcel, wideParcel);

        Machine inputMachine = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine);

        // Act
        ImportParcelResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.importParcelInvalids()).isEmpty();
        assertThat(result.machines()).hasSize(1);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(3);

        // Проверяем порядок размещения: широкая должна быть первой (внизу)
        Machine machine = result.machines().getFirst();
        var placedParcels = machine.parcels();

        // Широкая посылка 5x1 должна быть внизу (y=0)
        var widePlaced = placedParcels.stream()
                .filter(p -> p.parcel().getWidth() == 5)
                .findFirst()
                .orElseThrow();
        assertThat(widePlaced.y()).isZero();
    }

    @Test
    @DisplayName("Должен вернуть только одну успешную посылку, так как машин не хватает")
    void loadParcels_TwoParcelsOneDoesNotFit_ReturnsOneMachineAndOneInvalid() {
        // Arrange
        Parcel parcel1 = parcelBuilder.buildFromLines(List.of("11111", "11111", "11111", "11111")); // 5x4

        Parcel parcel2 = parcelBuilder.buildFromLines(List.of("222222", "222222", "222222")); // 6x3

        List<Parcel> parcels = List.of(parcel1, parcel2);

        Machine inputMachine = new Machine(6, 6);
        List<Machine> machines = List.of(inputMachine);

        // Act
        ImportParcelResult result = strategy.loadParcels(parcels, machines);

        // Assert
        assertThat(result).isNotNull();

        // Должны получить 1 заполненную машину
        assertThat(result.machines()).hasSize(1);
        assertThat(result.importParcelInvalids()).hasSize(1);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(1);
    }

    @Test
    @DisplayName("Должен вернуть правильный тип алгоритма")
    void getAlgorithmType_ReturnsCorrectType() {
        // Act & Assert
        assertThat(strategy.getAlgorithmType()).isEqualTo(ParcelLoadingStrategyType.DENSE_PACKING);
        assertThat(strategy.getAlgorithmName()).isEqualTo("Плотная упаковка");
    }
}
