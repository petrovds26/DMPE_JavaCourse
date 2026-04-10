package ru.hofftech.telegram.exception.advice;

import io.github.drednote.telegram.core.annotation.TelegramAdvice;
import io.github.drednote.telegram.core.annotation.TelegramExceptionHandler;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.type.ScenarioException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import ru.hofftech.telegram.exception.FeignException;
import ru.hofftech.telegram.exception.ValidateException;
import ru.hofftech.telegram.model.constant.InformationMessage;

/**
 * Глобальный обработчик исключений для Telegram бота.
 * <p>
 * Перехватывает исключения, возникающие при обработке обновлений,
 * и возвращает пользователю понятные сообщения об ошибках.
 */
@Slf4j
@Component
@TelegramAdvice
@NullMarked
public class TelegramExceptionAdvice {

    /**
     * Обрабатывает исключения, возникающие в сценариях.
     * <p>
     * Извлекает причину исключения и в зависимости от её типа формирует соответствующий ответ.
     *
     * @param e исключение сценария
     * @param request исходный запрос от Telegram
     * @return сообщение об ошибке для пользователя
     */
    @TelegramExceptionHandler(ScenarioException.class)
    public String handleScenarioException(ScenarioException e, UpdateRequest request) {
        Throwable cause = e.getCause();

        if (cause instanceof ValidateException || cause instanceof IllegalArgumentException) {
            log.warn("Ошибка валидации для chatId={}: {}", request.getChatId(), cause.getMessage());
            return String.format(InformationMessage.ERROR_MESSAGE, cause.getMessage());
        }

        if (cause instanceof FeignException) {
            log.error("Ошибка при обращении к внешнему сервису={}: {}", request.getChatId(), cause.getMessage());
            return cause.getMessage();
        }

        if (cause instanceof IllegalStateException) {
            log.error("Ошибка состояния для chatId={}: {}", request.getChatId(), cause.getMessage());
            return cause.getMessage();
        }

        log.error("Необработанная ScenarioException для chatId={}", request.getChatId(), e);
        return InformationMessage.UNEXPECTED_ERROR;
    }

    /**
     * Обрабатывает все неперехваченные исключения.
     *
     * @param e исключение
     * @param request исходный запрос от Telegram
     * @return стандартное сообщение о технической ошибке
     */
    @TelegramExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, UpdateRequest request) {
        log.error("Необработанная ошибка для chatId={}", request.getChatId(), e);
        return InformationMessage.UNEXPECTED_ERROR;
    }
}
