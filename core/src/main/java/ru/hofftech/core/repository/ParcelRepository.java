package ru.hofftech.core.repository;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Находит все посылки с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница с посылками
     */
    Page<ParcelEntity> findAll(Pageable pageable);
}
