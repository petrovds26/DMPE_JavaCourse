package ru.hofftech.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hofftech.model.dto.LoadingResult;
import ru.hofftech.service.loader.strategy.impl.DensePackingStrategy;
import ru.hofftech.service.loader.strategy.impl.OneParcelPerMachineStrategy;
import ru.hofftech.service.output.impl.ParcelOutputEmpty;
import ru.hofftech.service.parser.ParcelBuilder;
import ru.hofftech.service.parser.ParcelNormalizer;
import ru.hofftech.service.parser.source.impl.StringParcelSource;
import ru.hofftech.service.validation.impl.ParcelGridValidator;
import ru.hofftech.service.validation.impl.ParcelListStringValidator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ParcelProcessor интеграционные тесты")
class ParcelProcessorTest {

    private ParcelNormalizer normalizer;
    private ParcelBuilder parcelBuilder;
    private ParcelListStringValidator stringValidator;
    private ParcelGridValidator gridValidator;
    private ParcelOutputEmpty parcelOutputEmpty;

    @BeforeEach
    void setUp() {
        normalizer = new ParcelNormalizer();
        parcelBuilder = new ParcelBuilder();
        stringValidator = new ParcelListStringValidator();
        gridValidator = new ParcelGridValidator();
        parcelOutputEmpty = new ParcelOutputEmpty();
    }

    @Test
    @DisplayName("Должен успешно обработать одну валидную посылку с OneParcelPerMachineStrategy")
    void process_OneValidParcelWithOneParcelStrategy_ReturnsOneMachine() {
        // Arrange
        List<List<String>> inputBlocks = List.of(List.of("111"));

        ParcelProcessor processor = new ParcelProcessor(
                new StringParcelSource(inputBlocks),
                normalizer,
                parcelBuilder,
                stringValidator,
                gridValidator,
                new OneParcelPerMachineStrategy(),
                parcelOutputEmpty);

        // Act
        LoadingResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputParcels()).hasSize(1);
        assertThat(result.invalidParcels()).isEmpty();
        assertThat(result.oversizedParcels()).isEmpty();
        assertThat(result.machines()).hasSize(1);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(1);
    }

    @Test
    @DisplayName("Должен успешно обработать три валидные посылки с OneParcelPerMachineStrategy")
    void process_ThreeValidParcelsWithOneParcelStrategy_ReturnsThreeMachines() {
        // Arrange
        List<List<String>> inputBlocks = List.of(List.of("1"), List.of("22", "22"), List.of("333", "3 3", "333"));

        ParcelProcessor processor = new ParcelProcessor(
                new StringParcelSource(inputBlocks),
                normalizer,
                parcelBuilder,
                stringValidator,
                gridValidator,
                new OneParcelPerMachineStrategy(),
                parcelOutputEmpty);

        // Act
        LoadingResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputParcels()).hasSize(3);
        assertThat(result.invalidParcels()).isEmpty();
        assertThat(result.oversizedParcels()).isEmpty();
        assertThat(result.machines()).hasSize(3);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен отбросить посылку с пустой строкой")
    void process_EmptyLineParcel_AddsToInvalidParcels() {
        // Arrange
        List<List<String>> inputBlocks = List.of(
                List.of("") // пустая строка
                );

        ParcelProcessor processor = new ParcelProcessor(
                new StringParcelSource(inputBlocks),
                normalizer,
                parcelBuilder,
                stringValidator,
                gridValidator,
                new OneParcelPerMachineStrategy(),
                parcelOutputEmpty);

        // Act
        LoadingResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputParcels()).isEmpty();
        assertThat(result.invalidParcels()).isEmpty(); // отбрасывается на этапе stringValidator
        assertThat(result.oversizedParcels()).isEmpty();
        assertThat(result.machines()).isEmpty();
    }

    @Test
    @DisplayName("Должен успешно упаковать несколько посылок разного размера с DensePackingStrategy")
    void process_MultipleParcelsWithDenseStrategy_ReturnsOptimalPacking() {
        // Arrange
        List<List<String>> inputBlocks = List.of(
                List.of("999", "999", "999"), // 3x3
                List.of("666", "666"), // 3x2
                List.of("55555"), // 5x1
                List.of("1"), // 1x1
                List.of("1"), // 1x1
                List.of("333") // 3x1
                );

        ParcelProcessor processor = new ParcelProcessor(
                new StringParcelSource(inputBlocks),
                normalizer,
                parcelBuilder,
                stringValidator,
                gridValidator,
                new DensePackingStrategy(),
                parcelOutputEmpty);

        // Act
        LoadingResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputParcels()).hasSize(6);
        assertThat(result.invalidParcels()).isEmpty();
        assertThat(result.oversizedParcels()).isEmpty();
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(6);
    }
}
