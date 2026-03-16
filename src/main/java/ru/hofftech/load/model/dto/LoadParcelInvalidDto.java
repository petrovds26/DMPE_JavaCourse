package ru.hofftech.load.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.dto.ParcelDto;

@Builder
public record LoadParcelInvalidDto(@NonNull ParcelDto parcel, @NonNull String cause) {}
