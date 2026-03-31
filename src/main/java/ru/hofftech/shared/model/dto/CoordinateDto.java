package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Builder
public record CoordinateDto(int x, int y) {}
