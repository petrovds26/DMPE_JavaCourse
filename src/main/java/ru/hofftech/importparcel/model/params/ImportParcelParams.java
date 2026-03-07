package ru.hofftech.importparcel.model.params;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Builder
public record ImportParcelParams(
        @NonNull String inputFilePath,
        @Nullable String outputFilePath,
        @NonNull Integer strategyId,
        @NonNull Integer truckCount) {}
