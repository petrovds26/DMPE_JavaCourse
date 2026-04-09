package ru.hofftech.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hofftech.core.mapper.CoreMapper;
import ru.hofftech.core.model.core.Machine;
import ru.hofftech.core.model.core.Parcel;
import ru.hofftech.core.model.core.PlacedParcel;
import ru.hofftech.shared.model.dto.MachineDto;
import ru.hofftech.shared.model.dto.UnloadRequestDto;
import ru.hofftech.shared.model.dto.UnloadResponseDto;
import ru.hofftech.shared.model.dto.UnloadStatisticDto;
import ru.hofftech.shared.model.enums.BillingOperationType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис для выполнения операций разгрузки машин.
 * <p>
 * Обрабатывает запросы на разгрузку: извлекает посылки из машин,
 * подсчитывает статистику и записывает результат в биллинг.
 */
@Service
@RequiredArgsConstructor
@NullMarked
@Slf4j
public class UnloadService {
    private final CoreMapper coreMapper;
    private final BillingService billingService;

    @Value("${unload.price-segment}")
    private BigDecimal priceSegment;

    /**
     * Выполняет разгрузку машин.
     *
     * @param unloadRequestDto DTO с параметрами разгрузки
     * @return DTO с результатами разгрузки
     */
    public UnloadResponseDto unload(UnloadRequestDto unloadRequestDto) {
        log.debug("Начало разгрузки {} машин по посылкам", unloadRequestDto.machines());

        List<MachineDto> machinesDto = unloadRequestDto.machines();

        List<Machine> machines = coreMapper.machineDtoListToMachineList(machinesDto);

        List<Parcel> parcels = machines.stream()
                .flatMap(machine -> machine.parcels().stream())
                .map(PlacedParcel::parcel)
                .toList();

        Integer totalUnloadMachines = (int) machines.stream()
                .filter(machine -> !machine.parcels().isEmpty())
                .count();
        Integer totalParcelsProcessed = parcels.size();
        int totalFilledCells =
                parcels.stream().mapToInt(Parcel::getFilledCellsCount).sum();
        BigDecimal totalAmount = BigDecimal.valueOf(totalFilledCells).multiply(priceSegment);

        billingService.createBilling(
                unloadRequestDto.userId(),
                BillingOperationType.UNLOAD,
                totalUnloadMachines,
                totalParcelsProcessed,
                totalAmount);

        UnloadStatisticDto unloadStatisticDto = UnloadStatisticDto.builder()
                .totalSuccessUnloadParcels(totalParcelsProcessed)
                .totalUnloadMachines(totalUnloadMachines)
                .totalSegments(totalFilledCells)
                .priceSegment(priceSegment)
                .totalAmount(totalAmount)
                .build();

        return UnloadResponseDto.builder()
                .parcels(coreMapper.parcelListToParcelDtoList(parcels))
                .statistic(unloadStatisticDto)
                .build();
    }
}
