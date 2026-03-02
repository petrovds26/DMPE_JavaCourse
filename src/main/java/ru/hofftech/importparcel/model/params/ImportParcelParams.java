package ru.hofftech.importparcel.model.params;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record ImportParcelParams(
        @NonNull String inputFilePath,
        String outputFilePath,
        @NonNull Integer strategyId,
        @NonNull Integer truckCount) {}
