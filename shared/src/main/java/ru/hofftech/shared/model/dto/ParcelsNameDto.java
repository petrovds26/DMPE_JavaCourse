package ru.hofftech.shared.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * DTO списка посылок, содержащий только наименования.
 * <p>
 * Используется для загрузки списка посылок из JSON файла.
 *
 * @param parcelsName список наименований посылок
 */
@NullMarked
@Builder
public record ParcelsNameDto(
        @NotBlank(message = "Список посылок не может быть пустым")
                @Size(min = 1, message = "Должна быть передана хотя бы одна посылка")
                List<String> parcelsName) {}
