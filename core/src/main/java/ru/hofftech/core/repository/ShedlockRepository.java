package ru.hofftech.core.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.hofftech.core.model.entity.ShedlockEntity;

/**
 * JPA репозиторий для работы с распределёнными блокировками ShedLock.
 * <p>
 * Предоставляет методы для поиска блокировок по имени.
 */
public interface ShedlockRepository extends ListCrudRepository<ShedlockEntity, String> {
    /**
     * Находит блокировку по её имени.
     *
     * @param name имя блокировки
     * @return сущность блокировки или null, если не найдена
     */
    ShedlockEntity findByName(String name);
}
