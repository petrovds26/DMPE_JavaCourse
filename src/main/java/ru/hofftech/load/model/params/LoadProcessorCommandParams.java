package ru.hofftech.load.model.params;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.List;

@Builder
public record LoadProcessorCommandParams(
        @NonNull List<Parcel> parcels, @NonNull List<Machine> machines, @NonNull List<String> prevErrors) {}
