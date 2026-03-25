package ru.hofftech.load.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.dto.ParcelDto;

@Builder
@NullMarked
public record LoadParcelInvalidDto(ParcelDto parcel, String cause) {}
