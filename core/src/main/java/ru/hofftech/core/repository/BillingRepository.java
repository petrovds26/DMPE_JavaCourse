package ru.hofftech.core.repository;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hofftech.core.model.entity.BillingEntity;

import java.time.LocalDateTime;

/**
 * Репозиторий для работы с сущностью BillingEntity.
 */
@NullMarked
@Repository
public interface BillingRepository extends JpaRepository<BillingEntity, Long> {

    /**
     * Находит все операции биллинга для пользователя с пагинацией.
     *
     * @param userId   идентификатор пользователя
     * @param pageable параметры пагинации
     * @return страница с записями биллинга
     */
    Page<BillingEntity> findByUserIdOrderByCreatedDtDesc(String userId, Pageable pageable);

    /**
     * Находит все операции биллинга для пользователя за период между датами с пагинацией.
     *
     * @param userId   идентификатор пользователя
     * @param from     начало периода
     * @param to       конец периода
     * @param pageable параметры пагинации
     * @return страница с записями биллинга
     */
    Page<BillingEntity> findByUserIdAndCreatedDtBetweenOrderByCreatedDtDesc(
            String userId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    /**
     * Находит все операции биллинга для пользователя после указанной даты с пагинацией.
     *
     * @param userId   идентификатор пользователя
     * @param from     начальная дата
     * @param pageable параметры пагинации
     * @return страница с записями биллинга
     */
    Page<BillingEntity> findByUserIdAndCreatedDtAfterOrderByCreatedDtDesc(
            String userId, LocalDateTime from, Pageable pageable);

    /**
     * Находит все операции биллинга для пользователя до указанной даты с пагинацией.
     *
     * @param userId   идентификатор пользователя
     * @param to       конечная дата
     * @param pageable параметры пагинации
     * @return страница с записями биллинга
     */
    Page<BillingEntity> findByUserIdAndCreatedDtBeforeOrderByCreatedDtDesc(
            String userId, LocalDateTime to, Pageable pageable);
}
