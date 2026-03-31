package ru.hofftech.load.model.params;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.List;

@Builder
@NullMarked
public record LoadProcessorCommandParams(List<Parcel> parcels, List<Machine> machines, List<String> prevErrors) {}
