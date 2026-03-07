package ru.hofftech.shared.validation;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Общий интерфейс для всех валидаторов посылок
 * @param <T> тип валидируемого объекта
 */
public interface Validator<T> {

    /**
     * Валидирует объект и возвращает список ошибок
     * @param object объект для валидации
     * @return список ошибок (пустой список, если ошибок нет)
     */
    @NonNull
    List<String> validate(@Nullable T object);
}
