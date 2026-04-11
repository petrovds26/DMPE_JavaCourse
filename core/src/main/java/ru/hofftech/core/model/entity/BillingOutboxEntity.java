package ru.hofftech.core.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Сущность для хранения событий в таблице outbox.
 * <p>
 * Используется в паттерне Transactional Outbox для гарантированной
 * доставки событий в сервис биллинга через Kafka.
 */
@NullMarked
@Entity
@Table(name = "billing_outbox")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingOutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outboxKey;

    @NotNull
    private String payload;

    @Nullable
    private LocalDateTime sentDt;

    @CreationTimestamp
    private LocalDateTime createdDt;

    @UpdateTimestamp
    private LocalDateTime modifiedDt;
}
