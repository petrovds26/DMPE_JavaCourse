package ru.hofftech.importmachine.model.dto;

import lombok.Builder;
import lombok.NonNull;
import ru.hofftech.shared.model.dto.MachineDto;

import java.util.List;

@Builder
public record ImportMachineDto(@NonNull List<MachineDto> machines) {}
