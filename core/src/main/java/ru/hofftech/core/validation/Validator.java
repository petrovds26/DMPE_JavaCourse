package ru.hofftech.core.validation;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Общий интерфейс для всех валидаторов.
 * <p>
 * Предоставляет единый контракт для валидации объектов различных типов.
 *
 * @param <T> тип валидируемого объекта
 */
@NullMarked
public interface Validator<T> {

    /**
     * Валидирует объект и возвращает список ошибок.
     *
     * @param object объект для валидации (может быть null)
     * @return список ошибок (пустой список, если ошибок нет)
     */
    List<String> validate(@Nullable T object);
}
