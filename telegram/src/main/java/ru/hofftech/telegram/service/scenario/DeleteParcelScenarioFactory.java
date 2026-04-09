package ru.hofftech.telegram.service.scenario;

import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenario;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenarioAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.ParcelNameRequestDto;
import ru.hofftech.telegram.exception.FeignException;
import ru.hofftech.telegram.model.constant.InformationMessage;
import ru.hofftech.telegram.model.enums.State;
import ru.hofftech.telegram.service.CoreService;
import ru.hofftech.telegram.util.ActionContextUtil;
import ru.hofftech.telegram.util.MessageUtil;
import ru.hofftech.telegram.validation.InputParametersValidator;

/**
 * Фабрика сценариев для удаления посылки.
 * <p>
 * Реализует пошаговый диалог: ввод названия посылки для удаления.
 */
@Slf4j
@TelegramScenario
@RequiredArgsConstructor
@NullMarked
@SuppressWarnings("ClassCanBeRecord")
public class DeleteParcelScenarioFactory {
    private final InputParametersValidator inputParametersValidator;
    private final CoreService coreService;

    /**
     * Начинает процесс удаления посылки.
     * <p>
     * Очищает предыдущие данные диалога и запрашивает название посылки.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом названия посылки
     */
    @TelegramScenarioAction
    public SendMessage startDeleteParcel(ActionContext<?> context) {
        log.info("Начало удаления посылки, chatId={}", ActionContextUtil.getChatId(context));

        // Очищаем старые данные
        ActionContextUtil.getVariables(context).clear();

        return MessageUtil.createSendMessage(ActionContextUtil.getChatId(context), State.NAME.getDescription(), true);
    }

    /**
     * Удаляет посылку по введённому названию.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с результатом удаления посылки
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    @TelegramScenarioAction
    public SendMessage deleteParcel(ActionContext<?> context) {
        String name = ActionContextUtil.getText(context);

        inputParametersValidator.validateName(name);

        log.info("Удаление посылки: name={}", name);

        Response<String> response = coreService.deleteParcel(
                ParcelNameRequestDto.builder().name(name).build());

        if (response.isSuccess()) {
            return MessageUtil.createSendMessage(ActionContextUtil.getChatId(context), response.getData(), false);
        }

        throw new FeignException(InformationMessage.FEIGN_ERROR.formatted(
                response.getResult().getMessage(), response.getResult().getCode()));
    }
}
