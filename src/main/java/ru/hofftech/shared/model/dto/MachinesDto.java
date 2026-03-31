package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@Builder
public record MachinesDto(List<MachineDto> machines) {}
