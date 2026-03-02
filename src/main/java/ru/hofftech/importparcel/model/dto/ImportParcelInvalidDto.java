package ru.hofftech.importparcel.model.dto;

import lombok.Builder;
import lombok.NonNull;
import ru.hofftech.shared.model.dto.ParcelDto;

@Builder
public record ImportParcelInvalidDto(@NonNull ParcelDto parcel, @NonNull String cause) {}
