package ru.hofftech.importparcel.service.parser.machine.source.impl;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import ru.hofftech.importparcel.service.parser.machine.source.ImportParcelMachineSource;
import ru.hofftech.shared.model.core.Machine;

import java.util.ArrayList;
import java.util.List;

/**
 * Получаем набор машин по умолчанию в количестве numberOfMachines
 */
@RequiredArgsConstructor
public class ImportParcelMachineIntegerDefaultSource implements ImportParcelMachineSource<Integer> {

    @Override
    public @NonNull List<Machine> getMachines(@NonNull Integer numberOfMachines) {
        List<Machine> machines = new ArrayList<>();
        for (int i = 0; i < numberOfMachines; i++) {
            Machine newMachine = new Machine();
            machines.add(newMachine);
        }
        return machines;
    }
}
