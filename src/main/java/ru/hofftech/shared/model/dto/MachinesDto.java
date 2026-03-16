package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

import java.util.List;

@Builder
public record MachinesDto(@NonNull List<MachineDto> machines) {}
