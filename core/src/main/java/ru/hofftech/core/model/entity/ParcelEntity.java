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
import lombok.With;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDateTime;

/**
 * Сущность для хранения посылок в базе данных.
 * <p>
 * Содержит название, форму и символ посылки, а также временные метки.
 */
@NullMarked
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Entity
@With
@Table(name = "parcel")
public class ParcelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parcelKey;

    @NotNull
    private String name;

    @NotNull
    private String form;

    @NotNull
    private Character symbol;

    @CreationTimestamp
    private LocalDateTime createdDt;

    @UpdateTimestamp
    private LocalDateTime modifiedDt;
}
