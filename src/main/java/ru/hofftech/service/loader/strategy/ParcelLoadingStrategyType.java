package ru.hofftech.service.loader.strategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParcelLoadingStrategyType {
    ONE_PARCEL_PER_MACHINE(1, "Одна посылка на машину"),
    DENSE_PACKING(2, "Плотная укладка");

    private final int id;
    private final String description;
}
