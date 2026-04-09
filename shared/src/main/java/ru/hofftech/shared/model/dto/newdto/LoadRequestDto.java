package ru.hofftech.shared.model.dto.newdto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.enums.LoadStrategyType;

import java.util.List;

/**
 * DTO запроса на загрузку посылок в машины.
 * <p>
 * Содержит все необходимые параметры для выполнения операции загрузки.
 *
 * @param parcels      список названий посылок для загрузки
 * @param machines     список форм машин
 * @param userId       идентификатор пользователя (для биллинга)
 * @param loadStrategy стратегия загрузки
 */
@NullMarked
@Builder
public record LoadRequestDto(
        @Valid @Size(min = 1, message = "Должна быть указана хотя бы одна посылка") List<ParcelNameRequestDto> parcels,
        @Valid @Size(min = 1, message = "Должна быть указана хотя бы одна машина") List<MachineFormRequestDto> machines,
        @NotBlank(message = "Пользователь должен быть указан") String userId,
        @NotNull(message = "Стратегия должна быть указана") LoadStrategyType loadStrategy) {}
