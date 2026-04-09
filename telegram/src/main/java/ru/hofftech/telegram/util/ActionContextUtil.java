package ru.hofftech.telegram.util;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.springframework.statemachine.StateContext;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Map;

/**
 * Утилитарный класс для работы с ActionContext в сценариях Telegram бота.
 */
@UtilityClass
@NullMarked
public class ActionContextUtil {

    /**
     * Получает переменные состояния из ActionContext.
     * <p>
     * Переменные состояния сохраняются между шагами сценария и могут использоваться
     * для хранения данных пользователя в процессе диалога.
     *
     * @param context ActionContext сценария
     * @param <S> тип состояния
     * @return Map с переменными состояния
     */
    public static <S> Map<Object, Object> getVariables(ActionContext<S> context) {
        StateContext<S, ?> machineContext = context.getMachineContext();
        return machineContext.getExtendedState().getVariables();
    }

    /**
     * Получает текст сообщения из ActionContext.
     *
     * @param context ActionContext сценария
     * @param <S> тип состояния
     * @return текст сообщения
     */
    public static <S> String getText(ActionContext<S> context) {
        UpdateRequest request = context.getUpdateRequest();
        String text = request.getText();
        return text != null ? text : "";
    }

    /**
     * Получает идентификатор чата из ActionContext.
     *
     * @param context ActionContext сценария
     * @param <S> тип состояния
     * @return идентификатор чата
     */
    public static <S> Long getChatId(ActionContext<S> context) {
        return context.getUpdateRequest().getChatId();
    }

    /**
     * Получает имя пользователя из ActionContext.
     *
     * @param context ActionContext сценария
     * @param <S> тип состояния
     * @return имя пользователя, или "Пользователь" если имя не определено
     */
    public static <S> String getFirstName(ActionContext<S> context) {
        UpdateRequest request = context.getUpdateRequest();
        User user = request.getUser();
        if (user == null) {
            return "Пользователь";
        }
        return user.getFirstName();
    }

    /**
     * Получает исходный запрос из ActionContext.
     *
     * @param context ActionContext сценария
     * @param <S> тип состояния
     * @return объект UpdateRequest
     */
    public static <S> UpdateRequest getUpdateRequest(ActionContext<S> context) {
        return context.getUpdateRequest();
    }
}
