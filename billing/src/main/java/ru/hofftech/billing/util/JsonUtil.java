package ru.hofftech.billing.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.billing.exception.JsonUtilException;

/**
 * Утилитный класс для работы с JSON.
 * Предоставляет методы сериализации и десериализации объектов
 * с предварительно настроенным ObjectMapper.
 */
@NullMarked
@UtilityClass
public class JsonUtil {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // Игнорируем поля, которые могут создавать циклические ссылки
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * Десериализует JSON-строку в объект указанного типа с поддержкой дженериков.
     *
     * @param json     JSON-строка
     * @param <T>      тип целевого объекта
     * @return целевой объект
     * @throws JsonUtilException если произошла ошибка десериализации
     */
    @NonNull
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new JsonUtilException("Ошибка при десериализации JSON", e);
        }
    }

    /**
     * Десериализует JSON-строку в объект указанного класса.
     *
     * @param json     JSON-строка
     * @param classOfT класс целевого объекта
     * @param <T>      тип целевого объекта
     * @return целевой объект
     * @throws JsonUtilException если произошла ошибка десериализации
     */
    @NonNull
    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return objectMapper.readValue(json, classOfT);
        } catch (JsonProcessingException e) {
            throw new JsonUtilException("Ошибка при десериализации JSON", e);
        }
    }

    /**
     * Сериализует объект в JSON-строку с форматированием.
     *
     * @param src объект для сериализации (может быть null)
     * @return JSON-строка (не может быть null)
     * @throws JsonUtilException если произошла ошибка сериализации
     */
    public static String toJson(@Nullable Object src) {
        try {
            return objectMapper.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new JsonUtilException("Ошибка при сериализации в JSON", e);
        }
    }

    /**
     * Сериализует объект в массив байтов (JSON в UTF-8).
     *
     * @param src объект для сериализации (может быть null)
     * @return массив байтов (не может быть null)
     * @throws JsonUtilException если произошла ошибка сериализации
     */
    public static byte[] writeValueAsBytes(@Nullable Object src) {
        try {
            return objectMapper.writeValueAsBytes(src);
        } catch (JsonProcessingException e) {
            throw new JsonUtilException("Ошибка при преобразовании объекта в байты", e);
        }
    }
}
