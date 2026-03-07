package ru.hofftech.importparcel.model.core;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import ru.hofftech.importparcel.model.enums.ImportParcelInvalidCauseType;
import ru.hofftech.shared.model.core.Parcel;

@Builder
public record ImportParcelInvalid(
        @NonNull Parcel parcel, @NonNull ImportParcelInvalidCauseType causeType, @NonNull String cause) {}
