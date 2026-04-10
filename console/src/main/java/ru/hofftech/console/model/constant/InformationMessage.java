package ru.hofftech.console.model.constant;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;

/**
 * Константы информационных сообщений, используемых в приложении.
 * <p>
 * Содержит шаблоны сообщений для логирования и ответов API.
 */
@NullMarked
@UtilityClass
public final class InformationMessage {
    public static final String FEIGN_ERROR = "Ошибка: %s (Код: %s)";
}
