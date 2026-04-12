package ru.hofftech.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.hofftech.core.model.core.LoadStrategyParcelInvalid;
import ru.hofftech.core.model.core.Machine;
import ru.hofftech.core.model.core.Parcel;
import ru.hofftech.core.model.core.PlacedParcel;
import ru.hofftech.shared.model.dto.CoordinateDto;
import ru.hofftech.shared.model.dto.LoadParcelInvalidDto;
import ru.hofftech.shared.model.dto.MachineDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.PlacedParcelDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Центральный маппер для преобразования между core-сущностями и DTO.
 * <p>
 * Содержит маппинг для Parcel, Machine, PlacedParcel и связанных с ними DTO.
 * Использует MapStruct для автоматической генерации кода маппинга
 * и кастомные методы для сложных преобразований.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CoreMapper {

    /**
     * Преобразует Parcel в ParcelDto.
     *
     * @param parcel сущность посылки
     * @return DTO посылки
     */
    @Mapping(target = "coordinates", source = "grid", qualifiedByName = "parcelGridToCoordinatesDtoList")
    @Mapping(target = "form", source = "parcel", qualifiedByName = "parcelToForm")
    ParcelDto parcelToParcelDto(Parcel parcel);

    /**
     * Преобразует форму посылки в строку.
     *
     * @param parcel сущность посылки
     * @return строковое представление формы посылки
     */
    @Named("parcelToForm")
    default String parcelToForm(Parcel parcel) {
        return parcel.getForm();
    }

    /**
     * Преобразует grid посылки в список координат заполненных клеток.
     *
     * @param grid двумерный массив с формой посылки
     * @return список координат заполненных клеток
     */
    @Named("parcelGridToCoordinatesDtoList")
    default List<CoordinateDto> parcelGridToCoordinatesDtoList(boolean[][] grid) {
        List<CoordinateDto> coordinates = new ArrayList<>();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x]) {
                    coordinates.add(new CoordinateDto(x, y));
                }
            }
        }
        return coordinates;
    }

    /**
     * Преобразует ParcelDto обратно в Parcel.
     *
     * @param parcelDto DTO посылки
     * @return сущность посылки
     */
    @Mapping(target = "grid", source = "parcelDto", qualifiedByName = "parcelDtoToGrid")
    Parcel parcelDtoToParcel(ParcelDto parcelDto);

    /**
     * Восстанавливает grid из координат ParcelDto.
     *
     * @param dto DTO посылки
     * @return двумерный массив с формой посылки
     */
    @Named("parcelDtoToGrid")
    default boolean[][] parcelDtoToGrid(ParcelDto dto) {
        int height = dto.height();
        int width = dto.width();

        boolean[][] grid = new boolean[height][width];
        for (CoordinateDto coordinate : dto.coordinates()) {
            if (coordinate.y() < height && coordinate.x() < width) {
                grid[coordinate.y()][coordinate.x()] = true;
            }
        }
        return grid;
    }

    /**
     * Преобразует список ParcelDto в список Parcel.
     *
     * @param parcelDtoList список DTO посылок
     * @return список сущностей посылок
     */
    List<Parcel> parcelDtoListToParcelList(List<ParcelDto> parcelDtoList);

    /**
     * Преобразует список Parcel в список ParcelDto.
     *
     * @param parcelList список сущностей посылок
     * @return список DTO посылок
     */
    List<ParcelDto> parcelListToParcelDtoList(List<Parcel> parcelList);

    /**
     * Преобразует PlacedParcel в PlacedParcelDto.
     *
     * @param placedParcel сущность размещённой посылки
     * @return DTO размещённой посылки
     */
    PlacedParcelDto placedParcelToPlacedParcelDto(PlacedParcel placedParcel);

    /**
     * Преобразует PlacedParcelDto в PlacedParcel.
     *
     * @param dto DTO размещённой посылки
     * @return сущность размещённой посылки
     */
    PlacedParcel placedParcelDtoToPlacedParcel(PlacedParcelDto dto);

    /**
     * Преобразует список PlacedParcel в список PlacedParcelDto.
     *
     * @param placedParcels список сущностей размещённых посылок
     * @return список DTO размещённых посылок
     */
    List<PlacedParcelDto> placedParcelListToPlacedParcelDtoList(List<PlacedParcel> placedParcels);

    /**
     * Преобразует список PlacedParcelDto в список PlacedParcel.
     *
     * @param placedParcelDtoList список DTO размещённых посылок
     * @return список сущностей размещённых посылок
     */
    List<PlacedParcel> placedParcelDtoListToPlacedParcelList(List<PlacedParcelDto> placedParcelDtoList);

    /**
     * Преобразует Machine в MachineDto.
     *
     * @param machine сущность машины
     * @return DTO машины
     */
    @Mapping(target = "form", source = "machine", qualifiedByName = "machineToForm")
    MachineDto machineToMachineDto(Machine machine);

    /**
     * Преобразует содержимое машины в строковое представление.
     *
     * @param machine сущность машины
     * @return строковое представление машины
     */
    @Named("machineToForm")
    default String machineToForm(Machine machine) {
        List<String> lines = machine.getLines();
        return String.join("\n", lines);
    }

    /**
     * Преобразует MachineDto в Machine с восстановлением grid.
     *
     * @param machineDto DTO машины
     * @return сущность машины
     */
    @Mapping(target = "grid", source = "machineDto", qualifiedByName = "buildGridFromDto")
    Machine machineDtoToMachine(MachineDto machineDto);

    /**
     * Строит grid машины из DTO на основе размещённых посылок.
     *
     * @param machineDto DTO машины
     * @return двумерный массив с состоянием машины
     */
    @Named("buildGridFromDto")
    default char[][] buildGridFromDto(MachineDto machineDto) {
        int width = machineDto.width();
        int height = machineDto.height();

        // Сначала преобразуем DTO в core-объекты
        List<PlacedParcel> placedParcels = placedParcelDtoListToPlacedParcelList(machineDto.parcels());

        // Создаём пустой grid
        char[][] grid = new char[height][width];

        // Заполняем пустотой
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = ' ';
            }
        }

        // Размещаем посылки
        if (placedParcels == null) {
            return grid;
        }

        for (PlacedParcel placed : placedParcels) {
            Parcel parcel = placed.parcel();

            int startX = placed.x();
            int startY = placed.y();
            char symbol = parcel.symbol();
            boolean[][] parcelGrid = parcel.grid();

            for (int i = 0; i < parcel.getHeight(); i++) {
                for (int j = 0; j < parcel.getWidth(); j++) {
                    if (parcelGrid[i][j]) {
                        int targetX = startX + j;
                        int targetY = startY + i;

                        if (targetX >= 0 && targetX < width && targetY >= 0 && targetY < height) {
                            grid[targetY][targetX] = symbol;
                        }
                    }
                }
            }
        }

        return grid;
    }

    /**
     * Преобразует список Machine в список MachineDto.
     *
     * @param machines список сущностей машин
     * @return список DTO машин
     */
    List<MachineDto> machineListToMachineDtoList(List<Machine> machines);

    /**
     * Преобразует список MachineDto в список Machine.
     *
     * @param machinesDto список DTO машин
     * @return список сущностей машин
     */
    List<Machine> machineDtoListToMachineList(List<MachineDto> machinesDto);

    /**
     * Преобразует LoadStrategyParcelInvalid в LoadParcelInvalidDto.
     *
     * @param loadStrategyParcelInvalid сущность проблемной посылки
     * @return DTO проблемной посылки
     */
    LoadParcelInvalidDto loadStrategyParcelInvalidToDto(LoadStrategyParcelInvalid loadStrategyParcelInvalid);

    /**
     * Преобразует список LoadStrategyParcelInvalid в список LoadParcelInvalidDto.
     *
     * @param loadStrategyParcelInvalids список сущностей проблемных посылок
     * @return список DTO проблемных посылок
     */
    List<LoadParcelInvalidDto> loadStrategyParcelInvalidListToDto(
            List<LoadStrategyParcelInvalid> loadStrategyParcelInvalids);
}
