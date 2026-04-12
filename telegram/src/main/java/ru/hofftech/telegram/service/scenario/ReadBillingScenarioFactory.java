package ru.hofftech.telegram.service.scenario;

import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenario;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenarioAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.PageDto;
import ru.hofftech.shared.util.PrintStringUtil;
import ru.hofftech.telegram.exception.FeignException;
import ru.hofftech.telegram.model.constant.InformationMessage;
import ru.hofftech.telegram.model.enums.State;
import ru.hofftech.telegram.service.ExternalService;
import ru.hofftech.telegram.util.ActionContextUtil;
import ru.hofftech.telegram.util.MessageUtil;
import ru.hofftech.telegram.validation.InputParametersValidator;

import java.util.Map;

/**
 * Фабрика сценариев для получения истории биллинга.
 * <p>
 * Реализует пошаговый диалог: ввод идентификатора пользователя, даты начала и даты окончания периода.
 */
@Slf4j
@TelegramScenario
@RequiredArgsConstructor
@NullMarked
@SuppressWarnings("ClassCanBeRecord")
public class ReadBillingScenarioFactory {
    private final InputParametersValidator inputParametersValidator;
    private final ExternalService externalService;

    /**
     * Начинает процесс получения истории биллинга.
     * <p>
     * Очищает предыдущие данные диалога и запрашивает идентификатор пользователя.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом идентификатора пользователя
     */
    @TelegramScenarioAction
    public SendMessage startReadBilling(ActionContext<?> context) {
        log.info("Начало получения данных по биллингу, chatId={}", ActionContextUtil.getChatId(context));

        // Очищаем старые данные
        ActionContextUtil.getVariables(context).clear();

        return MessageUtil.createSendMessage(
                ActionContextUtil.getChatId(context), State.USER_ID.getDescription(), true);
    }

    /**
     * Сохраняет идентификатор пользователя и запрашивает дату начала периода.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом даты начала
     */
    @TelegramScenarioAction
    public SendMessage saveUserId(ActionContext<?> context) {
        String userId = ActionContextUtil.getText(context);

        inputParametersValidator.validateUserId(userId);

        ActionContextUtil.getVariables(context).put(State.USER_ID, userId.trim());

        log.info("Сохранено название: {}, variables={}", userId, ActionContextUtil.getVariables(context));
        return MessageUtil.createSendMessage(
                ActionContextUtil.getChatId(context), State.FROM_DATE.getDescription(), true);
    }

    /**
     * Сохраняет дату начала и запрашивает дату окончания периода.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом даты окончания
     */
    @TelegramScenarioAction
    public SendMessage saveFromDate(ActionContext<?> context) {
        String fromDate = ActionContextUtil.getText(context);

        inputParametersValidator.validDate(fromDate);

        ActionContextUtil.getVariables(context).put(State.FROM_DATE, fromDate);

        log.info("Сохранена дата начала: {}, variables={}", fromDate, ActionContextUtil.getVariables(context));
        return MessageUtil.createSendMessage(
                ActionContextUtil.getChatId(context), State.TO_DATE.getDescription(), true);
    }

    /**
     * Получает историю биллинга за указанный период.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с историей биллинга
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    @TelegramScenarioAction
    public SendMessage readBilling(ActionContext<?> context) {
        String toDate = ActionContextUtil.getText(context);

        inputParametersValidator.validDate(toDate);

        Map<Object, Object> variables = ActionContextUtil.getVariables(context);
        String userId = (String) variables.get(State.USER_ID);
        String fromDate = (String) variables.get(State.FROM_DATE);

        if (userId == null || fromDate == null) {
            log.error("Отсутствуют обязательные данные: userId={}, fromDate={}", userId, fromDate);
            return MessageUtil.createSendMessage(
                    ActionContextUtil.getChatId(context), InformationMessage.CLEAR_DIALOG_MESSAGE, false);
        }

        log.info("Запрос истории биллинга: userId={}, fromDate={}, toDate={}", userId, fromDate, toDate);

        Response<PageDto<BillingDto>> response = externalService.readBilling(userId, fromDate, toDate);

        if (response.isSuccess()) {
            return MessageUtil.createSendMessage(
                    ActionContextUtil.getChatId(context),
                    PrintStringUtil.formatBillingHistory(response.getData().content()),
                    false);
        }

        throw new FeignException(InformationMessage.FEIGN_ERROR.formatted(
                response.getResult().getMessage(), response.getResult().getCode()));
    }
}
