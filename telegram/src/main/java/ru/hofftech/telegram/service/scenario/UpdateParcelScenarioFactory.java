package ru.hofftech.telegram.service.scenario;

import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenario;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenarioAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.newdto.ParcelFormRequestDto;
import ru.hofftech.telegram.exception.FeignException;
import ru.hofftech.telegram.model.constant.InformationMessage;
import ru.hofftech.telegram.model.enums.State;
import ru.hofftech.telegram.service.CoreService;
import ru.hofftech.telegram.util.ActionContextUtil;
import ru.hofftech.telegram.util.MessageUtil;
import ru.hofftech.telegram.validation.InputParametersValidator;

import java.util.Map;

/**
 * Фабрика сценариев для обновления посылки.
 * <p>
 * Реализует пошаговый диалог: ввод названия, символа и формы посылки для обновления.
 */
@Slf4j
@TelegramScenario
@RequiredArgsConstructor
@NullMarked
@SuppressWarnings("ClassCanBeRecord")
public class UpdateParcelScenarioFactory {
    private final InputParametersValidator inputParametersValidator;
    private final CoreService coreService;

    /**
     * Начинает процесс обновления посылки.
     * <p>
     * Очищает предыдущие данные диалога и запрашивает название посылки.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом названия посылки
     */
    @TelegramScenarioAction
    public SendMessage startUpdateParcel(ActionContext<?> context) {
        log.info("Начало обновления посылки, chatId={}", ActionContextUtil.getChatId(context));

        // Очищаем старые данные
        ActionContextUtil.getVariables(context).clear();

        return MessageUtil.createSendMessage(ActionContextUtil.getChatId(context), State.NAME.getDescription(), true);
    }

    /**
     * Сохраняет введённое название и запрашивает символ посылки.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом символа посылки
     */
    @TelegramScenarioAction
    public SendMessage saveName(ActionContext<?> context) {
        String name = ActionContextUtil.getText(context);

        inputParametersValidator.validateName(name);

        ActionContextUtil.getVariables(context).put(State.NAME, name.trim());

        log.info("Сохранено название: {}, variables={}", name, ActionContextUtil.getVariables(context));
        return MessageUtil.createSendMessage(ActionContextUtil.getChatId(context), State.SYMBOL.getDescription(), true);
    }

    /**
     * Сохраняет введённый символ и запрашивает форму посылки.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом формы посылки
     */
    @TelegramScenarioAction
    public SendMessage saveSymbol(ActionContext<?> context) {
        String symbol = ActionContextUtil.getText(context);

        inputParametersValidator.validateParcelSymbol(symbol);

        ActionContextUtil.getVariables(context).put(State.SYMBOL, symbol);

        log.info("Сохранён символ: {}, variables={}", symbol, ActionContextUtil.getVariables(context));
        return MessageUtil.createSendMessage(ActionContextUtil.getChatId(context), State.FORM.getDescription(), true);
    }

    /**
     * Обновляет посылку из сохранённых данных.
     * <p>
     * Вызывает Core сервис для обновления посылки и возвращает результат.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с результатом обновления посылки
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    @TelegramScenarioAction
    public SendMessage updateParcel(ActionContext<?> context) {
        String form = ActionContextUtil.getText(context);

        inputParametersValidator.validateParcelForm(form);

        Map<Object, Object> variables = ActionContextUtil.getVariables(context);
        String name = (String) variables.get(State.NAME);
        String symbol = (String) variables.get(State.SYMBOL);

        if (name == null || symbol == null) {
            log.error("Отсутствуют обязательные данные: name={}, symbol={}", name, symbol);
            return MessageUtil.createSendMessage(
                    ActionContextUtil.getChatId(context), InformationMessage.CLEAR_DIALOG_MESSAGE, false);
        }

        log.info("Обновление посылки: name={}, symbol={}, form={}", name, symbol, form);

        Response<String> response = coreService.updateParcel(ParcelFormRequestDto.builder()
                .name(name)
                .form(form)
                .symbol(symbol)
                .build());

        if (response.isSuccess()) {
            return MessageUtil.createSendMessage(ActionContextUtil.getChatId(context), response.getData(), false);
        }

        throw new FeignException(InformationMessage.FEIGN_ERROR.formatted(
                response.getResult().getMessage(), response.getResult().getCode()));
    }
}
