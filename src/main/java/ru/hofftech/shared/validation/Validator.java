package ru.hofftech.shared.validation;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Общий интерфейс для всех валидаторов.
 *
 * @param <T> тип валидируемого объекта
 */
@NullMarked
public interface Validator<T> {

    /**
     * Валидирует объект и возвращает список ошибок.
     *
     * @param object объект для валидации (может быть null)
     * @return список ошибок (пустой список, если ошибок нет) (не может быть null)
     */
    List<String> validate(@Nullable T object);
}
