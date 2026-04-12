package ru.hofftech.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hofftech.core.exception.LoadException;
import ru.hofftech.core.mapper.CoreMapper;
import ru.hofftech.core.mapper.ParcelEntityMapper;
import ru.hofftech.core.model.core.LoadResult;
import ru.hofftech.core.model.core.Machine;
import ru.hofftech.core.model.core.Parcel;
import ru.hofftech.core.model.core.ParserMachineProcessorResult;
import ru.hofftech.core.model.core.ParserParcelProcessorResult;
import ru.hofftech.core.repository.ParcelRepository;
import ru.hofftech.core.service.loader.strategy.LoadStrategy;
import ru.hofftech.core.service.loader.strategy.LoadStrategyService;
import ru.hofftech.core.service.parcer.ParserMachine;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.LoadRequestDto;
import ru.hofftech.shared.model.dto.LoadResponseDto;
import ru.hofftech.shared.model.dto.LoadStatisticDto;
import ru.hofftech.shared.model.dto.MachineFormRequestDto;
import ru.hofftech.shared.model.dto.ParcelNameRequestDto;
import ru.hofftech.shared.model.enums.BillingOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для выполнения операций загрузки посылок в машины.
 * <p>
 * Обрабатывает запросы на загрузку: парсит входные данные,
 * применяет стратегию упаковки и записывает результат в биллинг.
 */
@Service
@RequiredArgsConstructor
@NullMarked
@Slf4j
public class LoadService {
    private final ParserMachine parserMachine;
    private final LoadStrategyService loadStrategyService;
    private final ParcelRepository parcelRepository;
    private final BillingOutboxService billingOutboxService;
    private final ParcelEntityMapper parcelEntityMapper;
    private final CoreMapper coreMapper;

    @Value("${load.price-segment}")
    private BigDecimal priceSegment;

    /**
     * Выполняет загрузку посылок в машины.
     *
     * @param loadRequestDto DTO с параметрами загрузки
     * @return DTO с результатами загрузки
     * @throws LoadException если не найдены машины, посылки или стратегия
     */
    public LoadResponseDto load(LoadRequestDto loadRequestDto) {
        // Запуск расчета загрузки машин
        log.debug(
                "Начало упаковки {} посылок по алгоритму: {}",
                loadRequestDto.parcels().size(),
                loadRequestDto.loadStrategy().getDescription());

        ParserMachineProcessorResult machineResult = parserMachine(loadRequestDto.machines());

        log.info(
                "Распознано машин: {}, ошибок: {}",
                machineResult.machines().size(),
                machineResult.errors().size());

        if (machineResult.machines().isEmpty()) {
            throw new LoadException("Не найден ни одна машина. Ошибки: %s".formatted(machineResult.errors()));
        }

        LoadStrategy loadStrategy = loadStrategyService.getStrategyById(
                loadRequestDto.loadStrategy().getId());

        if (loadStrategy == null) {
            throw new LoadException("Не найден алгоритм загрузки машины %s. Доступные стратегии: %s"
                    .formatted(
                            loadRequestDto.loadStrategy().getId(),
                            loadStrategyService.getAvailableStrategiesDescription()));
        }

        ParserParcelProcessorResult parcelResult = findParcel(loadRequestDto.parcels());

        log.info(
                "Распознано посылок: {}, ошибок: {}",
                parcelResult.parcels().size(),
                parcelResult.errors().size());

        if (parcelResult.parcels().isEmpty()) {
            throw new LoadException("Не найден ни одна посылка. Ошибки: %s".formatted(parcelResult.errors()));
        }

        LoadResult result = loadStrategy.loadParcels(parcelResult.parcels(), machineResult.machines());

        Integer totalUsedMachines = result.getUsedMachinesCount();
        Integer totalInputParcels = loadRequestDto.parcels().size();
        int totalFilledCells = result.getTotalFilledCells();
        BigDecimal totalAmount = BigDecimal.valueOf(totalFilledCells).multiply(priceSegment);

        BillingDto billingDto = BillingDto.builder()
                .userId(loadRequestDto.userId())
                .operationType(BillingOperationType.LOAD)
                .machineCount(totalUsedMachines)
                .parcelCount(totalInputParcels)
                .totalAmount(totalAmount)
                .createdDt(LocalDateTime.now())
                .build();

        billingOutboxService.saveEvent(billingDto);

        LoadStatisticDto loadStatisticDto = LoadStatisticDto.builder()
                .errors(result.errors())
                .invalidParcels(coreMapper.loadStrategyParcelInvalidListToDto(result.loadStrategyParcelInvalids()))
                .totalInputParcels(totalInputParcels)
                .totalSuccessLoadParcels(result.getTotalParcelsProcessed())
                .totalUsedMachines(totalUsedMachines)
                .totalSegments(totalFilledCells)
                .priceSegment(priceSegment)
                .totalAmount(totalAmount)
                .build();

        return LoadResponseDto.builder()
                .machines(coreMapper.machineListToMachineDtoList(result.machines()))
                .statistic(loadStatisticDto)
                .build();
    }

    /**
     * Парсит список DTO машин в сущности Machine.
     *
     * @param machines список DTO машин
     * @return результат парсинга с машинами и ошибками
     */
    public ParserMachineProcessorResult parserMachine(List<MachineFormRequestDto> machines) {
        List<Machine> allMachines = new ArrayList<>();
        List<String> allErrors = new ArrayList<>();

        for (MachineFormRequestDto machineDto : machines) {
            ParserMachineProcessorResult result = parserMachine.transform(machineDto.form());
            Optional.of(result.machines()).ifPresent(allMachines::addAll);
            if (result.hasErrors()) {
                allErrors.addAll(result.errors());
            }
        }

        return ParserMachineProcessorResult.builder()
                .machines(allMachines)
                .errors(allErrors)
                .build();
    }

    /**
     * Находит посылки по названиям в репозитории.
     *
     * @param parcels список DTO с названиями посылок
     * @return результат поиска с посылками и ошибками
     */
    public ParserParcelProcessorResult findParcel(List<ParcelNameRequestDto> parcels) {
        List<Parcel> allParcels = new ArrayList<>();
        List<String> allErrors = new ArrayList<>();

        for (ParcelNameRequestDto parcelDto : parcels) {
            parcelRepository
                    .findByName(parcelDto.name())
                    .ifPresentOrElse(
                            entity -> allParcels.add(parcelEntityMapper.toParcel(entity)),
                            () -> allErrors.add("Посылка не найдена: %s".formatted(parcelDto.name())));
        }

        return ParserParcelProcessorResult.builder()
                .parcels(allParcels)
                .errors(allErrors)
                .build();
    }
}
