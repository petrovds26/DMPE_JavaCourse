package ru.hofftech.importmachine.model.params;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Builder
public record ImportMachineParams(@NonNull String inputFilePath, @Nullable String outputFilePath) {}
