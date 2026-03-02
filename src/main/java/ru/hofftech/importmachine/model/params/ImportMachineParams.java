package ru.hofftech.importmachine.model.params;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record ImportMachineParams(@NonNull String inputFilePath, String outputFilePath) {}
