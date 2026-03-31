package ru.hofftech.load.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.load.model.params.LoadProcessorCommandParams;
import ru.hofftech.load.service.loader.strategy.LoadStrategy;
import ru.hofftech.load.service.output.LoadPrepareOutputResult;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.service.command.ProcessorCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Процессорная команда для погрузки посылок.
 * Выполняет бизнес-логику упаковки посылок в машины по выбранной стратегии.
 *
 */
@Slf4j
@RequiredArgsConstructor
// Рекорд не может быть создан с интерфейсом
@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public class LoadParcelProcessorCommand implements ProcessorCommand<LoadProcessorCommandParams> {
    private final LoadStrategy strategy;

    private final LoadPrepareOutputResult loadPrepareOutputResult;

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult execute(LoadProcessorCommandParams loadParams) {
        // Запуск расчета загрузки машин
        log.debug(
                "Начало упаковки {} посылок по алгоритму: {}",
                loadParams.parcels().size(),
                strategy.getAlgorithmName());

        LoadResult packingResult = strategy.loadParcels(loadParams.parcels(), loadParams.machines());

        List<String> errors = new ArrayList<>(loadParams.prevErrors());

        if (packingResult.errors() != null) {
            errors.addAll(packingResult.errors());
        }

        LoadResult loadResult = LoadResult.builder()
                .machines(packingResult.machines())
                .loadStrategyParcelInvalids(packingResult.loadStrategyParcelInvalids())
                .inputParcels(loadParams.parcels())
                .errors(errors)
                .build();

        return loadPrepareOutputResult.output(loadResult);
    }
}
