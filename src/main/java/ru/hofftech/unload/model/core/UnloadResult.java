package ru.hofftech.unload.model.core;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.List;

/**
 * Результат упаковки посылок
 */
@Builder
@NullMarked
public record UnloadResult(
        List<Machine> inputMachines, // Машины, поступившие на вход
        List<Parcel> parcels // Успешно загруженные посылки
        ) {}
