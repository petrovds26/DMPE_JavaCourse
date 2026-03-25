package ru.hofftech.readparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.command.ProcessorCommand;
import ru.hofftech.shared.util.PrintStringUtil;

import java.util.List;
import java.util.Optional;

/**
 * Процессорная команда для чтения посылок.
 */
@Slf4j
@RequiredArgsConstructor
@NullMarked
@SuppressWarnings("ClassCanBeRecord")
public class ReadParcelProcessorCommand implements ProcessorCommand<String> {
    private final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult execute(@Nullable String name) {

        if (name == null) {
            List<Parcel> parcels = parcelRepository.findAll();
            if (parcels.isEmpty()) {
                return ProcessorCommandResult.createFailure("Посылки не найдены");
            }
            StringBuilder sb = new StringBuilder();
            for (Parcel parcel : parcels) {
                sb.append(PrintStringUtil.parcelRender(parcel)).append("\n");
            }
            return ProcessorCommandResult.createSuccess(sb.toString());
        } else {
            Optional<Parcel> parcel = parcelRepository.find(name);
            return parcel.map(value -> ProcessorCommandResult.createSuccess(PrintStringUtil.parcelRender(value)))
                    .orElseGet(() -> ProcessorCommandResult.createFailure(
                            String.format("Посылка с названием %s не существует", name)));
        }
    }
}
