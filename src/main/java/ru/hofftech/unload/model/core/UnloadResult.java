package ru.hofftech.unload.model.core;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.List;

/**
 * Результат упаковки посылок
 */
@Builder
public record UnloadResult(
        @NonNull List<Machine> inputMachines, // Машины, поступившие на вход
        @NonNull List<Parcel> parcels // Успешно загруженные посылки
        ) {}
