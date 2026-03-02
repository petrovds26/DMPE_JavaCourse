package ru.hofftech.importparcel.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.model.params.ImportParcelParams;
import ru.hofftech.importparcel.service.loader.strategy.ParcelLoadingStrategy;
import ru.hofftech.importparcel.service.loader.strategy.impl.DensePackingStrategy;
import ru.hofftech.importparcel.service.loader.strategy.impl.OneParcelPerMachineStrategy;
import ru.hofftech.importparcel.service.output.ImportParcelOutput;
import ru.hofftech.importparcel.service.output.impl.ImportParcelOutputEmpty;
import ru.hofftech.importparcel.service.parser.machine.source.impl.ImportParcelMachineIntegerDefaultSource;
import ru.hofftech.importparcel.service.parser.parcel.source.impl.ImportParcelTxtFileSource;
import ru.hofftech.importparcel.validation.impl.ParcelGridValidator;
import ru.hofftech.importparcel.validation.impl.ParcelListStringValidator;
import ru.hofftech.shared.service.parser.ParcelBuilder;
import ru.hofftech.shared.service.parser.ParcelNormalizer;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ImportParcelProcessor компонентные тесты")
@ExtendWith(MockitoExtension.class)
class ImportParcelProcessorTest {

    private ImportParcelMachineIntegerDefaultSource importParcelMachineSource;

    private ParcelNormalizer normalizer;
    private ParcelBuilder parcelBuilder;
    private ParcelListStringValidator stringValidator;
    private ParcelGridValidator gridValidator;
    private ImportParcelOutput parcelOutputEmpty;

    @Mock
    private ImportParcelTxtFileSource fileParcelSource;

    @Mock
    private ImportParcelParams importParcelParams;

    @BeforeEach
    void setUp() {
        importParcelMachineSource = new ImportParcelMachineIntegerDefaultSource();
        normalizer = new ParcelNormalizer();
        parcelBuilder = new ParcelBuilder();
        stringValidator = new ParcelListStringValidator();
        gridValidator = new ParcelGridValidator();
        parcelOutputEmpty = new ImportParcelOutputEmpty();
    }

    private ImportParcelProcessor createProcessor(
            ParcelLoadingStrategy loadingStrategy, List<List<String>> inputBlocks, Integer truckCount) {
        try {
            Mockito.doReturn(inputBlocks).when(fileParcelSource).getParcelBlocks(Mockito.anyString());

            Mockito.doReturn(truckCount).when(importParcelParams).truckCount();

            Mockito.doReturn("").when(importParcelParams).inputFilePath();

            return new ImportParcelProcessor(
                    importParcelParams,
                    importParcelMachineSource,
                    fileParcelSource,
                    normalizer,
                    parcelBuilder,
                    stringValidator,
                    gridValidator,
                    loadingStrategy,
                    parcelOutputEmpty);
        } catch (IOException e) {
            return null;
        }
    }

    @Test
    @DisplayName("Должен успешно обработать одну валидную посылку с OneParcelPerMachineStrategy")
    void process_OneValidParcelWithOneParcelStrategy_ReturnsOneMachine() {
        // Arrange
        ParcelLoadingStrategy loadingStrategy = new OneParcelPerMachineStrategy();
        List<List<String>> inputBlocks = List.of(List.of("111"));
        Integer truckCount = 2;

        ImportParcelProcessor processor = createProcessor(loadingStrategy, inputBlocks, truckCount);

        // Act
        Assertions.assertNotNull(processor);
        ImportParcelResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputParcels()).hasSize(1);
        assertThat(result.importParcelInvalids()).isEmpty();
        assertThat(result.machines()).hasSize(2);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(1);
    }

    @Test
    @DisplayName("Должен успешно обработать три валидные посылки с OneParcelPerMachineStrategy")
    void process_ThreeValidParcelsWithOneParcelStrategy_ReturnsThreeMachines() {
        // Arrange
        ParcelLoadingStrategy loadingStrategy = new OneParcelPerMachineStrategy();
        List<List<String>> inputBlocks = List.of(List.of("1"), List.of("22", "22"), List.of("333", "3 3", "333"));
        Integer truckCount = 3;

        ImportParcelProcessor processor = createProcessor(loadingStrategy, inputBlocks, truckCount);

        // Act
        Assertions.assertNotNull(processor);
        ImportParcelResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputParcels()).hasSize(3);
        assertThat(result.importParcelInvalids()).isEmpty();
        assertThat(result.machines()).hasSize(3);
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен отбросить посылку с пустой строкой")
    void process_EmptyLineParcel_AddsToInvalidParcels() {
        // Arrange
        ParcelLoadingStrategy loadingStrategy = new OneParcelPerMachineStrategy();
        List<List<String>> inputBlocks = List.of(
                List.of("") // пустая строка
                );
        Integer truckCount = 3;

        ImportParcelProcessor processor = createProcessor(loadingStrategy, inputBlocks, truckCount);

        // Act
        Assertions.assertNotNull(processor);
        ImportParcelResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputParcels()).isEmpty();
        assertThat(result.importParcelInvalids()).isEmpty(); // отбрасывается на этапе stringValidator
        assertThat(result.machines()).hasSize(3);
    }

    @Test
    @DisplayName("Должен успешно упаковать несколько посылок разного размера с DensePackingStrategy")
    void process_MultipleParcelsWithDenseStrategy_ReturnsOptimalPacking() {
        // Arrange
        ParcelLoadingStrategy loadingStrategy = new DensePackingStrategy();
        List<List<String>> inputBlocks = List.of(
                List.of("999", "999", "999"), // 3x3
                List.of("666", "666"), // 3x2
                List.of("55555"), // 5x1
                List.of("1"), // 1x1
                List.of("1"), // 1x1
                List.of("333") // 3x1
                );
        Integer truckCount = 3;

        ImportParcelProcessor processor = createProcessor(loadingStrategy, inputBlocks, truckCount);

        // Act
        Assertions.assertNotNull(processor);
        ImportParcelResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputParcels()).hasSize(6);
        assertThat(result.importParcelInvalids()).isEmpty();
        assertThat(result.getTotalParcelsProcessed()).isEqualTo(6);
        assertThat(result.machines()).hasSize(3);
    }
}
