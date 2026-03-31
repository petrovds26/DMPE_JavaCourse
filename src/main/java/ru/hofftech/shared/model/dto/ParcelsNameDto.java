package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * DTO списка посылок, которая содержит только наименования.
 * Используется для загрузки списка посылок из JSON файла.
 *
 * @param parcelsName список наименований посылок (не может быть null)
 */
@NullMarked
@Builder
public record ParcelsNameDto(List<String> parcelsName) {}
