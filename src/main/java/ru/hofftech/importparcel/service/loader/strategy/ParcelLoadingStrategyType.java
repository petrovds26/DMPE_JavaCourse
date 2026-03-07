package ru.hofftech.importparcel.service.loader.strategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor
public enum ParcelLoadingStrategyType {
    ONE_PARCEL_PER_MACHINE(1, "Одна посылка на машину"),
    DENSE_PACKING(2, "Плотная укладка"),
    BALANCED_PACKING(3, "Равномерная погрузка");

    private final int id;

    @NonNull
    private final String description;
}
