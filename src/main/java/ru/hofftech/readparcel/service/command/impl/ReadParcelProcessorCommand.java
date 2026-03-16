package ru.hofftech.readparcel.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
@SuppressWarnings("ClassCanBeRecord")
public class ReadParcelProcessorCommand implements ProcessorCommand<String> {
    @NonNull
    private final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull ProcessorCommandResult execute(@Nullable String name) {

        if (name == null) {
            List<Parcel> parcels = parcelRepository.findAll();
            if (parcels.isEmpty()) {
                return ProcessorCommandResult.builder()
                        .success(false)
                        .message("Посылки не найдены")
                        .build();
            }
            StringBuilder sb = new StringBuilder();
            for (Parcel parcel : parcels) {
                sb.append(PrintStringUtil.parcelRender(parcel)).append("\n");
            }
            return ProcessorCommandResult.builder()
                    .success(true)
                    .message(sb.toString())
                    .build();
        } else {
            Optional<Parcel> parcel = parcelRepository.find(name);
            if (parcel.isEmpty()) {
                return ProcessorCommandResult.builder()
                        .success(false)
                        .message(String.format("Посылка с названием %s не существует", name))
                        .build();
            } else {
                return ProcessorCommandResult.builder()
                        .success(true)
                        .message(PrintStringUtil.parcelRender(parcel.get()))
                        .build();
            }
        }
    }
}
