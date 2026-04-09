package ru.hofftech.shared.model.dto.newdto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.dto.MachineDto;

import java.util.List;

/**
 * DTO запроса на разгрузку машин.
 *
 * @param machines список машин для разгрузки
 * @param userId   идентификатор пользователя
 */
@NullMarked
@Builder
public record UnloadRequestDto(
        @Valid @Size(min = 1, message = "Должна быть указана хотя бы одна машина") List<MachineDto> machines,
        @NotBlank(message = "Пользователь должен быть указан") String userId) {}
