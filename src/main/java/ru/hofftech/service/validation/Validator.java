package ru.hofftech.service.validation;

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
    List<String> validate(T object);
}
