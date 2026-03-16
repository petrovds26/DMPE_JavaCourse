package ru.hofftech.shared.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.PlacedParcel;
import ru.hofftech.shared.model.dto.MachineDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.PlacedParcelDto;
import ru.hofftech.shared.service.parser.ParserParcelBuilder;
import ru.hofftech.shared.service.parser.ParserParcelNormalizer;

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
     * @param parcel сущность посылки (может быть null)
     * @return DTO посылки или null, если входной параметр null
     */
    @Nullable
    public ParcelDto parcelToDto(@Nullable Parcel parcel) {
        if (parcel == null) {
            return null;
        }

        String form = String.join("\n", parcel.getForm());

        return ParcelDto.builder().name(parcel.getName()).form(form).build();
    }

    /**
     * Преобразует Machine в MachineDto.
     *
     * @param machine сущность машины (может быть null)
     * @return DTO машины или null, если входной параметр null
     */
    @Nullable
    public MachineDto machineToDto(@Nullable Machine machine) {
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
     * Преобразует список машин в список DTO.
     *
     * @param machines список машин (может быть null)
     * @return список DTO (не может быть null)
     */
    @NonNull
    public List<MachineDto> machinesToDto(@Nullable List<Machine> machines) {
        if (machines == null) {
            return List.of();
        }
        return machines.stream().map(MapperUtil::machineToDto).toList();
    }

    /**
     * Преобразует PlacedParcel в PlacedParcelDto.
     *
     * @param placedParcel размещённая посылка (может быть null)
     * @return DTO размещённой посылки или null, если входной параметр null
     */
    @Nullable
    public PlacedParcelDto placedParcelToDto(@Nullable PlacedParcel placedParcel) {
        if (placedParcel == null) {
            return null;
        }

        ParcelDto parcelDto = MapperUtil.parcelToDto(placedParcel.parcel());

        if (parcelDto == null) {
            return null;
        }

        return PlacedParcelDto.builder()
                .parcel(parcelDto)
                .x(placedParcel.x())
                .y(placedParcel.y())
                .build();
    }

    /**
     * Преобразует MachineDto в Machine.
     *
     * @param dto DTO машины (может быть null)
     * @return сущность машины или null, если входной параметр null
     */
    @Nullable
    public Machine dtoToMachine(@Nullable MachineDto dto) {
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
     * Преобразует список PlacedParcelDto в список PlacedParcel.
     *
     * @param dtos список DTO размещённых посылок (может быть null)
     * @return список сущностей (не может быть null)
     */
    @NonNull
    public List<PlacedParcel> dtoToPlacedParcels(@Nullable List<PlacedParcelDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(MapperUtil::dtoToPlacedParcel).toList();
    }

    /**
     * Преобразует PlacedParcelDto в PlacedParcel.
     *
     * @param dto DTO размещённой посылки (может быть null)
     * @return сущность размещённой посылки или null, если входной параметр null
     */
    @Nullable
    public PlacedParcel dtoToPlacedParcel(@Nullable PlacedParcelDto dto) {
        if (dto == null) {
            return null;
        }

        Parcel parcel = dtoToParcel(dto.parcel());

        if (parcel == null) {
            return null;
        }

        return PlacedParcel.builder().parcel(parcel).x(dto.x()).y(dto.y()).build();
    }

    /**
     * Преобразует ParcelDto в Parcel.
     * Выполняет нормализацию строк и построение grid.
     *
     * @param dto DTO посылки (может быть null)
     * @return сущность посылки или null, если входной параметр null
     */
    @Nullable
    public Parcel dtoToParcel(@Nullable ParcelDto dto) {
        if (dto == null) {
            return null;
        }

        String form = dto.form();

        if (form == null) {
            return null;
        }

        List<String> lines = Arrays.asList(form.split("\n"));
        List<String> normalizedLines = new ParserParcelNormalizer().normalize(lines);
        return new ParserParcelBuilder().buildFromLines(dto.name(), normalizedLines);
    }

    /**
     * Преобразует список ParcelDto в список Parcel.
     *
     * @param dtos список DTO посылок (может быть null)
     * @return список сущностей (не может быть null)
     */
    @NonNull
    public List<Parcel> dtoToParcels(@Nullable List<ParcelDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(MapperUtil::dtoToParcel).toList();
    }

    /**
     * Создаёт grid машины из списка размещённых посылок.
     *
     * @param width         ширина машины
     * @param height        высота машины
     * @param placedParcels список размещённых посылок (не может быть null)
     * @return grid машины (не может быть null)
     */
    private char @NonNull [] @NonNull [] createGridFromPlacedParcels(
            int width, int height, @NonNull List<PlacedParcel> placedParcels) {
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
     * Преобразует список MachineDto в список Machine.
     *
     * @param dtos список DTO машин (может быть null)
     * @return список сущностей (не может быть null)
     */
    @NonNull
    public List<Machine> dtoToMachines(@Nullable List<MachineDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(MapperUtil::dtoToMachine).toList();
    }

    /**
     * Преобразует размещённые посылки в DTO.
     *
     * @param machine машина (не может быть null)
     * @return список DTO размещённых посылок (не может быть null)
     */
    @NonNull
    private List<PlacedParcelDto> convertPlacedParcels(@NonNull Machine machine) {
        return machine.parcels().stream().map(MapperUtil::placedParcelToDto).toList();
    }
}
