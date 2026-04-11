package ru.hofftech.core.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * Сущность для хранения информации о распределённых блокировках ShedLock.
 * <p>
 * Используется для координации выполнения запланированных задач
 * в кластеризованной среде.
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Entity
@Table(name = "shedlock")
public class ShedlockEntity {
    @Id
    private String name;

    private LocalDateTime lockUntil;

    private LocalDateTime lockedAt;
}
