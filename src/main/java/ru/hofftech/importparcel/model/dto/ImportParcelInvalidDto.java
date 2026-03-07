package ru.hofftech.importparcel.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.dto.ParcelDto;

@Builder
public record ImportParcelInvalidDto(@NonNull ParcelDto parcel, @NonNull String cause) {}
