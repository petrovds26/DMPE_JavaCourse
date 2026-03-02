package ru.hofftech.shared.util;

import lombok.experimental.UtilityClass;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.PlacedParcel;
import ru.hofftech.shared.model.dto.MachineDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.PlacedParcelDto;
import ru.hofftech.shared.service.parser.ParcelBuilder;
import ru.hofftech.shared.service.parser.ParcelNormalizer;

import java.util.Arrays;
import java.util.List;

/**
 * Утилитный класс для преобразования между сущностями и DTO.
 * Содержит методы для конвертации Parcel, Machine, PlacedParcel
 * в соответствующие DTO и обратно.
 */
@UtilityClass
public class MapperUtil {

    /**
     * Преобразует Parcel в ParcelDto.
     *
     * @param parcel сущность посылки
     * @return DTO посылки или null, если входной параметр null
     */
    public ParcelDto parcelToDto(Parcel parcel) {
        if (parcel == null) {
            return null;
        }

        String form = String.join("\n", parcel.getForm());

        return ParcelDto.builder().form(form).build();
    }

    /**
     * Преобразует Machine в MachineDto
     */
    public MachineDto machineToDto(Machine machine) {
        if (machine == null) {
            return null;
        }

        return MachineDto.builder()
                .parcels(convertPlacedParcels(machine))
                .width(machine.width())
                .height(machine.height())
                .build();
    }

    /**
     * Преобразует список машин в список DTO
     */
    public List<MachineDto> machinesToDto(List<Machine> machines) {
        if (machines == null) {
            return List.of();
        }
        return machines.stream().map(MapperUtil::machineToDto).toList();
    }

    /**
     * Преобразует PlacedParcel в PlacedParcelDto
     */
    public PlacedParcelDto placedParcelToDto(PlacedParcel placedParcel) {
        if (placedParcel == null) {
            return null;
        }

        return PlacedParcelDto.builder()
                .parcel(MapperUtil.parcelToDto(placedParcel.parcel()))
                .x(placedParcel.x())
                .y(placedParcel.y())
                .build();
    }

    /**
     * Преобразует MachineDto в Machine
     */
    public Machine dtoToMachine(MachineDto dto) {
        if (dto == null) {
            return null;
        }

        List<PlacedParcel> placedParcels = dtoToPlacedParcels(dto.parcels());
        char[][] grid = createGridFromPlacedParcels(dto.width(), dto.height(), placedParcels);

        return Machine.builder()
                .grid(grid)
                .parcels(placedParcels)
                .width(dto.width())
                .height(dto.height())
                .build();
    }

    /**
     * Преобразует список PlacedParcelDto в список PlacedParcel
     */
    public List<PlacedParcel> dtoToPlacedParcels(List<PlacedParcelDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(MapperUtil::dtoToPlacedParcel).toList();
    }

    /**
     * Преобразует PlacedParcelDto в PlacedParcel
     */
    public PlacedParcel dtoToPlacedParcel(PlacedParcelDto dto) {
        if (dto == null) {
            return null;
        }

        return PlacedParcel.builder()
                .parcel(dtoToParcel(dto.parcel()))
                .x(dto.x())
                .y(dto.y())
                .build();
    }

    /**
     * Преобразует ParcelDto в Parcel.
     * Выполняет нормализацию строк и построение grid.
     *
     * @param dto DTO посылки
     * @return сущность посылки или null, если входной параметр null
     */
    public Parcel dtoToParcel(ParcelDto dto) {
        if (dto == null) {
            return null;
        }

        List<String> lines = Arrays.asList(dto.form().split("\n"));
        List<String> normalizedLines = new ParcelNormalizer().normalize(lines);
        return new ParcelBuilder().buildFromLines(normalizedLines);
    }

    /**
     * Преобразует список ParcelDto в список Parcel
     */
    public List<Parcel> dtoToParcels(List<ParcelDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(MapperUtil::dtoToParcel).toList();
    }

    /**
     * Создаёт grid машины из списка размещённых посылок
     */
    private char[][] createGridFromPlacedParcels(int width, int height, List<PlacedParcel> placedParcels) {
        char[][] grid = new char[height][width];

        // Заполняем пустотой
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = ' ';
            }
        }

        // Размещаем посылки
        for (PlacedParcel placed : placedParcels) {
            Parcel parcel = placed.parcel();
            int startX = placed.x();
            int startY = placed.y();

            for (int i = 0; i < parcel.getHeight(); i++) {
                for (int j = 0; j < parcel.getWidth(); j++) {
                    if (parcel.grid()[i][j]) {
                        grid[startY + i][startX + j] = parcel.symbol();
                    }
                }
            }
        }

        return grid;
    }

    /**
     * Преобразует список MachineDto в список Machine
     */
    public List<Machine> dtoToMachines(List<MachineDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(MapperUtil::dtoToMachine).toList();
    }

    /**
     * Преобразует размещённые посылки в DTO
     */
    private List<PlacedParcelDto> convertPlacedParcels(Machine machine) {
        return machine.parcels().stream().map(MapperUtil::placedParcelToDto).toList();
    }
}
