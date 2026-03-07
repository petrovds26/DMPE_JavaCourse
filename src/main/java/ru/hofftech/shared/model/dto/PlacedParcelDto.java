package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

/**
 * DTO для передачи данных о размещённой в машине посылке.
 * Содержит DTO посылки и её координаты.
 */
@Builder
public record PlacedParcelDto(@NonNull ParcelDto parcel, int x, int y) {}
