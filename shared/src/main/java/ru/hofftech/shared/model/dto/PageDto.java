package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * DTO для пагинированного ответа.
 *
 * @param content       содержимое текущей страницы
 * @param totalElements общее количество элементов
 * @param totalPages    общее количество страниц
 * @param currentPage   номер текущей страницы (0-indexed)
 * @param pageSize      размер страницы
 * @param hasNext       есть ли следующая страница
 * @param hasPrevious   есть ли предыдущая страница
 */
@NullMarked
@Builder
public record PageDto<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious) {}
