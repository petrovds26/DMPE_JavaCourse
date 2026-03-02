package ru.hofftech.importmachine.model.core;

import lombok.Builder;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;

import java.util.List;

/**
 * Результат упаковки посылок
 */
@Builder
public record ImportMachineResult(
        List<Machine> inputMachines, // Машины, поступившие на вход
        List<Parcel> parcels // Успешно загруженные посылки
        ) {}
