package ru.hofftech.core.repository;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hofftech.core.model.entity.BillingOutboxEntity;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью BillingOutboxEntity.
 */
@NullMarked
@Repository
public interface BillingOutboxRepository extends JpaRepository<BillingOutboxEntity, UUID> {

    /**
     * Находит все неотправленные события.
     * <p>
     * Возвращает записи, у которых sentDt = NULL,
     * отсортированные по дате создания по возрастанию.
     *
     * @return список неотправленных событий
     */
    List<BillingOutboxEntity> findBySentDtIsNullOrderByCreatedDtAsc();
}
