package ru.hofftech.telegram.model.constant;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;

/**
 * Константы информационных сообщений, используемых в приложении.
 */
@NullMarked
@UtilityClass
public final class InformationMessage {
    public static final String FEIGN_ERROR = "Ошибка: %s (Код: %s)";
    public static final String ERROR_MESSAGE = "Ошибка: %s. Попробуйте снова.";
    public static final String CANCEL_MESSAGE = "Диалог отменён.";
    public static final String UNEXPECTED_ERROR = "Произошла техническая ошибка. Попробуйте позже.";
    public static final String CLEAR_DIALOG_MESSAGE = "Ошибка: данные диалога потеряны. Начните заново";
}
