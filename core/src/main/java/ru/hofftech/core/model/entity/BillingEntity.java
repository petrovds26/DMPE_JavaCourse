package ru.hofftech.core.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.With;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.enums.BillingOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность для хранения записей биллинга в базе данных.
 * <p>
 * Содержит информацию о платежах за операции погрузки и разгрузки.
 */
@NullMarked
@Entity
@Table(name = "billing")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@With
@FieldNameConstants
public class BillingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billingId;

    @NotNull
    private String userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BillingOperationType operationType;

    @NotNull
    private Integer machineCount;

    @NotNull
    private Integer parcelCount;

    @NotNull
    private BigDecimal totalAmount;

    @CreationTimestamp
    private LocalDateTime createdDt;

    @UpdateTimestamp
    private LocalDateTime modifiedDt;
}
