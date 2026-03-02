package ru.hofftech.importparcel.model.dto;

import lombok.Builder;
import lombok.NonNull;
import ru.hofftech.shared.model.dto.ParcelDto;

import java.util.List;

@Builder
public record ImportParcelDto(@NonNull List<ParcelDto> parcels) {}
