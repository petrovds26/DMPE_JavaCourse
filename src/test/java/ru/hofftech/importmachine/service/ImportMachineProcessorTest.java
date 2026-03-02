package ru.hofftech.importmachine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hofftech.importmachine.model.core.ImportMachineResult;
import ru.hofftech.importmachine.model.params.ImportMachineParams;
import ru.hofftech.importmachine.service.output.ImportMachineOutput;
import ru.hofftech.importmachine.service.output.impl.ImportMachineOutputEmpty;
import ru.hofftech.importmachine.service.parser.source.ImportMachineFileSource;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.dto.MachineDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.PlacedParcelDto;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@DisplayName("ImportMachineProcessor компонентные тесты")
@ExtendWith(MockitoExtension.class)
class ImportMachineProcessorTest {

    @Mock
    private ImportMachineFileSource<String> fileMachineSource;

    @Mock
    private ImportMachineParams importMachineParams;

    private ImportMachineOutput importMachineOutput;

    @BeforeEach
    void setUp() {
        importMachineOutput = new ImportMachineOutputEmpty();
    }

    private ImportMachineProcessor createProcessor(List<MachineDto> machinesDto) {
        try {
            Mockito.doReturn(machinesDto).when(fileMachineSource).getMachines(anyString());

            Mockito.doReturn("test.json").when(importMachineParams).inputFilePath();

            Mockito.doReturn(null).when(importMachineParams).outputFilePath();

            return new ImportMachineProcessor(importMachineParams, fileMachineSource, importMachineOutput);
        } catch (IOException e) {
            return null;
        }
    }

    private ParcelDto createParcelDto(String form) {
        return ParcelDto.builder().form(form).build();
    }

    private PlacedParcelDto createPlacedParcelDto(ParcelDto parcelDto, int x, int y) {
        return PlacedParcelDto.builder().parcel(parcelDto).x(x).y(y).build();
    }

    private MachineDto createMachineDto(List<PlacedParcelDto> placedParcels, int width, int height) {
        return MachineDto.builder()
                .parcels(placedParcels)
                .width(width)
                .height(height)
                .build();
    }

    @Test
    @DisplayName("Должен успешно обработать одну машину с одной посылкой")
    void process_OneMachineWithOneParcel_ReturnsCorrectResult() {
        // Arrange
        ParcelDto parcelDto = createParcelDto("111");
        PlacedParcelDto placedParcelDto = createPlacedParcelDto(parcelDto, 0, 0);
        MachineDto machineDto = createMachineDto(List.of(placedParcelDto), 6, 6);
        List<MachineDto> machinesDto = List.of(machineDto);

        ImportMachineProcessor processor = createProcessor(machinesDto);

        // Act
        assertThat(processor).isNotNull();
        ImportMachineResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputMachines()).hasSize(1);
        assertThat(result.parcels()).hasSize(1);

        Parcel parcel = result.parcels().getFirst();
        assertThat(parcel.symbol()).isEqualTo('1');
        assertThat(parcel.getWidth()).isEqualTo(3);
        assertThat(parcel.getHeight()).isEqualTo(1);
        assertThat(parcel.getForm()).isEqualTo("111");
    }

    @Test
    @DisplayName("Должен успешно обработать одну машину с несколькими посылками")
    void process_OneMachineWithMultipleParcels_ReturnsAllParcels() {
        // Arrange
        ParcelDto parcelDto1 = createParcelDto("99\n99");
        ParcelDto parcelDto2 = createParcelDto("333\n333\n333");

        PlacedParcelDto placedParcelDto1 = createPlacedParcelDto(parcelDto1, 0, 0);
        PlacedParcelDto placedParcelDto2 = createPlacedParcelDto(parcelDto2, 2, 2);

        MachineDto machineDto = createMachineDto(List.of(placedParcelDto1, placedParcelDto2), 6, 6);
        List<MachineDto> machinesDto = List.of(machineDto);

        ImportMachineProcessor processor = createProcessor(machinesDto);

        // Act
        assertThat(processor).isNotNull();
        ImportMachineResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputMachines()).hasSize(1);
        assertThat(result.parcels()).hasSize(2);
    }

    @Test
    @DisplayName("Должен успешно обработать несколько машин с посылками")
    void process_MultipleMachinesWithParcels_ReturnsAllParcels() {
        // Arrange
        // Машина 1: посылка 2x2
        ParcelDto parcelDto1 = createParcelDto("22\n22");
        PlacedParcelDto placedParcelDto1 = createPlacedParcelDto(parcelDto1, 0, 0);
        MachineDto machineDto1 = createMachineDto(List.of(placedParcelDto1), 6, 6);

        // Машина 2: посылки 3x3 и 1x1
        ParcelDto parcelDto2 = createParcelDto("333\n333\n333");
        ParcelDto parcelDto3 = createParcelDto("1");

        PlacedParcelDto placedParcelDto2 = createPlacedParcelDto(parcelDto2, 0, 0);
        PlacedParcelDto placedParcelDto3 = createPlacedParcelDto(parcelDto3, 3, 3);
        MachineDto machineDto2 = createMachineDto(List.of(placedParcelDto2, placedParcelDto3), 6, 6);

        List<MachineDto> machinesDto = List.of(machineDto1, machineDto2);

        ImportMachineProcessor processor = createProcessor(machinesDto);

        // Act
        assertThat(processor).isNotNull();
        ImportMachineResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputMachines()).hasSize(2);
        assertThat(result.parcels()).hasSize(3); // 1 + 2 посылки
    }

    @Test
    @DisplayName("Должен корректно обработать машину с пустыми слотами")
    void process_MachineWithEmptySlots_ReturnsOnlyPlacedParcels() {
        // Arrange
        ParcelDto parcelDto = createParcelDto("888\n8 8\n888");
        PlacedParcelDto placedParcelDto = createPlacedParcelDto(parcelDto, 1, 1);
        MachineDto machineDto = createMachineDto(List.of(placedParcelDto), 6, 6);
        List<MachineDto> machinesDto = List.of(machineDto);

        ImportMachineProcessor processor = createProcessor(machinesDto);

        // Act
        assertThat(processor).isNotNull();
        ImportMachineResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputMachines()).hasSize(1);
        assertThat(result.parcels()).hasSize(1);

        Parcel parcel = result.parcels().getFirst();
        assertThat(parcel.symbol()).isEqualTo('8');
        assertThat(parcel.getWidth()).isEqualTo(3);
        assertThat(parcel.getHeight()).isEqualTo(3);

        // Проверяем, что форма посылки сохранилась (с дыркой посередине)
        assertThat(parcel.getForm()).isEqualTo("888\n8 8\n888");
    }

    @Test
    @DisplayName("Должен вернуть пустой результат при отсутствии машин")
    void process_NoMachines_ReturnsEmptyResult() {
        // Arrange
        List<MachineDto> machinesDto = List.of();

        ImportMachineProcessor processor = createProcessor(machinesDto);

        // Act
        assertThat(processor).isNotNull();
        ImportMachineResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputMachines()).isEmpty();
        assertThat(result.parcels()).isEmpty();
    }

    @Test
    @DisplayName("Должен корректно обработать машины разных размеров")
    void process_MachinesWithDifferentSizes_ReturnsAllParcels() {
        // Arrange
        // Машина 6x6
        ParcelDto parcelDto1 = createParcelDto("111");
        PlacedParcelDto placedParcelDto1 = createPlacedParcelDto(parcelDto1, 0, 0);
        MachineDto machineDto1 = createMachineDto(List.of(placedParcelDto1), 6, 6);

        // Машина 4x4
        ParcelDto parcelDto2 = createParcelDto("22\n22");
        PlacedParcelDto placedParcelDto2 = createPlacedParcelDto(parcelDto2, 0, 0);
        MachineDto machineDto2 = createMachineDto(List.of(placedParcelDto2), 4, 4);

        List<MachineDto> machinesDto = List.of(machineDto1, machineDto2);

        ImportMachineProcessor processor = createProcessor(machinesDto);

        // Act
        assertThat(processor).isNotNull();
        ImportMachineResult result = processor.process();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.inputMachines()).hasSize(2);
        assertThat(result.parcels()).hasSize(2);

        // Проверяем, что размеры машин сохранились
        Machine machine1 = result.inputMachines().get(0);
        Machine machine2 = result.inputMachines().get(1);

        assertThat(machine1.width()).isEqualTo(6);
        assertThat(machine1.height()).isEqualTo(6);
        assertThat(machine2.width()).isEqualTo(4);
        assertThat(machine2.height()).isEqualTo(4);
    }
}
