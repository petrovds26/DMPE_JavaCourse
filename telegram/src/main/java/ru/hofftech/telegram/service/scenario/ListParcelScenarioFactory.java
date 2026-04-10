package ru.hofftech.telegram.service.scenario;

import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenario;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenarioAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.PageDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.util.PrintStringUtil;
import ru.hofftech.telegram.exception.FeignException;
import ru.hofftech.telegram.model.constant.InformationMessage;
import ru.hofftech.telegram.service.CoreService;
import ru.hofftech.telegram.util.ActionContextUtil;
import ru.hofftech.telegram.util.MessageUtil;

import java.util.stream.Collectors;

/**
 * Фабрика сценариев для получения списка всех посылок.
 */
@Slf4j
@TelegramScenario
@RequiredArgsConstructor
@NullMarked
@SuppressWarnings("ClassCanBeRecord")
public class ListParcelScenarioFactory {
    private final CoreService coreService;

    /**
     * Получает и отображает список всех посылок.
     *
     * @param context контекст выполнения сценария
     * @return сообщение со списком посылок
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    @TelegramScenarioAction
    public SendMessage listParcel(ActionContext<?> context) {
        log.info("Чтение всех посылок");
        Response<PageDto<ParcelDto>> response = coreService.readAllParcels();

        if (response.isSuccess()) {
            String responseText = "Список посылок:\n"
                    + response.getData().content().stream()
                            .map(PrintStringUtil::parcelRender)
                            .collect(Collectors.joining("\n"));
            return MessageUtil.createSendMessage(ActionContextUtil.getChatId(context), responseText, false);
        }

        throw new FeignException(InformationMessage.FEIGN_ERROR.formatted(
                response.getResult().getMessage(), response.getResult().getCode()));
    }
}
