package ru.hofftech.core.repository;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hofftech.core.model.entity.ParcelEntity;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью ParcelEntity.
 */
@NullMarked
@Repository
public interface ParcelRepository extends JpaRepository<ParcelEntity, Long> {

    /**
     * Находит посылку по названию.
     *
     * @param name название посылки
     * @return Optional с найденной посылкой
     */
    Optional<ParcelEntity> findByName(String name);

    /**
     * Проверяет существование посылки по названию.
     *
     * @param name название посылки
     * @return true если существует
     */
    boolean existsByName(String name);

    /**
     * Удаляет посылку по названию.
     *
     * @param name название посылки
     */
    void deleteByName(String name);
}
