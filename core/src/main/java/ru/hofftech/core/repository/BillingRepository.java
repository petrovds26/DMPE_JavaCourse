package ru.hofftech.core.repository;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hofftech.core.model.entity.BillingEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с сущностью BillingEntity.
 */
@NullMarked
@Repository
public interface BillingRepository extends JpaRepository<BillingEntity, Long> {

    /**
     * Находит все операции биллинга для пользователя за период между датами.
     *
     * @param userId идентификатор пользователя
     * @param from   начало периода
     * @param to     конец периода
     * @return список записей биллинга, отсортированных по убыванию даты
     */
    List<BillingEntity> findByUserIdAndCreatedDtBetweenOrderByCreatedDtDesc(
            String userId, LocalDateTime from, LocalDateTime to);

    /**
     * Находит все операции биллинга для пользователя после указанной даты.
     *
     * @param userId идентификатор пользователя
     * @param from   начальная дата
     * @return список записей биллинга, отсортированных по убыванию даты
     */
    List<BillingEntity> findByUserIdAndCreatedDtAfterOrderByCreatedDtDesc(String userId, LocalDateTime from);

    /**
     * Находит все операции биллинга для пользователя до указанной даты.
     *
     * @param userId идентификатор пользователя
     * @param to     конечная дата
     * @return список записей биллинга, отсортированных по убыванию даты
     */
    List<BillingEntity> findByUserIdAndCreatedDtBeforeOrderByCreatedDtDesc(String userId, LocalDateTime to);

    /**
     * Находит все операции биллинга для пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список записей биллинга, отсортированных по убыванию даты
     */
    List<BillingEntity> findByUserIdOrderByCreatedDtDesc(String userId);
}
