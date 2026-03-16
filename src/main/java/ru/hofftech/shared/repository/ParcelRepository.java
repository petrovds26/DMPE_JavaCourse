package ru.hofftech.shared.repository;

import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Parcel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий для хранения посылок в памяти.
 * Реализован на основе HashMap, обеспечивает потокобезопасность через копирование коллекций.
 */
public class ParcelRepository {
    @NonNull
    private final Map<String, Parcel> parcels;

    /**
     * Создаёт новый пустой репозиторий.
     */
    public ParcelRepository() {
        parcels = new HashMap<>();
    }

    /**
     * Добавляет новую посылку в репозиторий.
     *
     * @param parcel посылка для добавления (не может быть null)
     */
    public void insert(@NonNull Parcel parcel) {
        if (!parcels.containsKey(parcel.name())) {
            parcels.put(parcel.name(), parcel);
        }
    }

    /**
     * Обновляет существующую посылку в репозитории.
     *
     * @param parcel посылка с обновлёнными данными (не может быть null)
     */
    public void update(@NonNull Parcel parcel) {
        if (parcels.containsKey(parcel.name())) {
            parcels.put(parcel.name(), parcel);
        }
    }

    /**
     * Удаляет посылку по названию.
     *
     * @param name название посылки (не может быть null)
     */
    public void delete(@NonNull String name) {
        parcels.remove(name);
    }

    /**
     * Находит посылку по названию.
     *
     * @param name название посылки (не может быть null)
     * @return Optional с найденной посылкой (не может быть null)
     */
    @NonNull
    public Optional<Parcel> find(@NonNull String name) {
        if (parcels.containsKey(name)) {
            return Optional.of(parcels.get(name));
        }
        return Optional.empty();
    }

    /**
     * Возвращает список всех посылок.
     *
     * @return список посылок (не может быть null)
     */
    @NonNull
    public List<Parcel> findAll() {
        return List.copyOf(parcels.values());
    }
}
