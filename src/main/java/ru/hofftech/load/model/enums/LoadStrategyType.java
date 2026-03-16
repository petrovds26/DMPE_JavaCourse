package ru.hofftech.load.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor
public enum LoadStrategyType {
    ONE_PARCEL_PER_MACHINE(1, "Одна посылка на машину"),
    DENSE_PACKING(2, "Плотная укладка"),
    BALANCED_PACKING(3, "Равномерная погрузка");

    private final int id;

    @NonNull
    private final String description;
}
