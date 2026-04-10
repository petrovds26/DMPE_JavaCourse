package ru.hofftech.telegram.service.scenario;

import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenario;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenarioAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.util.PrintStringUtil;
import ru.hofftech.telegram.exception.FeignException;
import ru.hofftech.telegram.model.constant.InformationMessage;
import ru.hofftech.telegram.model.enums.State;
import ru.hofftech.telegram.service.CoreService;
import ru.hofftech.telegram.util.ActionContextUtil;
import ru.hofftech.telegram.util.MessageUtil;
import ru.hofftech.telegram.validation.InputParametersValidator;

import java.util.List;

/**
 * Фабрика сценариев для чтения посылки по названию.
 * <p>
 * Реализует пошаговый диалог: ввод названия посылки.
 */
@Slf4j
@TelegramScenario
@RequiredArgsConstructor
@NullMarked
@SuppressWarnings("ClassCanBeRecord")
public class ReadParcelScenarioFactory {
    private final InputParametersValidator inputParametersValidator;
    private final CoreService coreService;

    /**
     * Начинает процесс чтения посылки.
     * <p>
     * Очищает предыдущие данные диалога и запрашивает название посылки.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом названия посылки
     */
    @TelegramScenarioAction
    public SendMessage startReadParcel(ActionContext<?> context) {
        log.info("Начало чтения посылки, chatId={}", ActionContextUtil.getChatId(context));

        // Очищаем старые данные
        ActionContextUtil.getVariables(context).clear();

        return MessageUtil.createSendMessage(ActionContextUtil.getChatId(context), State.NAME.getDescription(), true);
    }

    /**
     * Читает посылку по введённому названию.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с информацией о посылке
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    @TelegramScenarioAction
    public SendMessage readParcel(ActionContext<?> context) {
        String name = ActionContextUtil.getText(context);

        inputParametersValidator.validateName(name);

        log.info("Чтение посылки: name={}", name);

        Response<List<ParcelDto>> response = coreService.readParcelByName(name);

        if (response.isSuccess()) {
            return MessageUtil.createSendMessage(
                    ActionContextUtil.getChatId(context),
                    PrintStringUtil.parcelRender(response.getData().getFirst()),
                    false);
        }

        throw new FeignException(InformationMessage.FEIGN_ERROR.formatted(
                response.getResult().getMessage(), response.getResult().getCode()));
    }
}
