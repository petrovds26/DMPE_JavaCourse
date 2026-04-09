package ru.hofftech.core.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import ru.hofftech.shared.model.dto.PageDto;

@NullMarked
@UtilityClass
public class PageDtoUtil {
    /**
     * Создаёт PageDto из Spring Page.
     *
     * @param page страница из Spring Data
     * @param <T> тип содержимого
     * @return DTO пагинированного ответа
     */
    public static <T> PageDto<T> from(Page<T> page) {
        return PageDto.<T>builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
