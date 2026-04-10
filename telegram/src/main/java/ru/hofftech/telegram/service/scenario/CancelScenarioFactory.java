package ru.hofftech.telegram.service.scenario;

import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenario;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenarioAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hofftech.telegram.model.constant.InformationMessage;
import ru.hofftech.telegram.util.ActionContextUtil;
import ru.hofftech.telegram.util.MessageUtil;

/**
 * Фабрика сценариев для отмены текущего диалога.
 */
@Slf4j
@TelegramScenario
@RequiredArgsConstructor
@NullMarked
public class CancelScenarioFactory {

    /**
     * Отменяет текущий диалог.
     * <p>
     * Очищает все сохранённые данные диалога и уведомляет пользователя об отмене.
     *
     * @param context контекст выполнения сценария
     * @return сообщение об отмене диалога
     */
    @TelegramScenarioAction
    public SendMessage cancelDialog(ActionContext<?> context) {
        ActionContextUtil.getVariables(context).clear();
        log.info("Отмена диалога, chatId={}", ActionContextUtil.getChatId(context));
        return MessageUtil.createSendMessage(
                ActionContextUtil.getChatId(context), InformationMessage.CANCEL_MESSAGE, false);
    }
}
