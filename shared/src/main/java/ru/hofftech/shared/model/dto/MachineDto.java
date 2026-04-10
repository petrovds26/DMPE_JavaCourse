package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * DTO для передачи данных о машине между сервисами.
 * <p>
 * Содержит полную информацию о состоянии машины:
 * размещённые посылки, строковое представление формы и размеры.
 *
 * @param parcels список размещённых в машине посылок с координатами
 * @param form    строковое представление текущего состояния машины
 * @param width   ширина машины в клетках
 * @param height  высота машины в клетках
 */
@NullMarked
@Builder
public record MachineDto(List<PlacedParcelDto> parcels, String form, Integer width, Integer height) {}
