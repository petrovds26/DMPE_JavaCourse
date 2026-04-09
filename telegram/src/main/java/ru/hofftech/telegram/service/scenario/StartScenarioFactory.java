package ru.hofftech.telegram.service.scenario;

import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenario;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenarioAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hofftech.telegram.util.ActionContextUtil;
import ru.hofftech.telegram.util.MessageUtil;
import ru.hofftech.telegram.util.TelegramKeyboardUtil;

/**
 * Фабрика сценариев для команды /start.
 * <p>
 * Отображает приветственное сообщение и основную клавиатуру с командами.
 */
@Slf4j
@TelegramScenario
@RequiredArgsConstructor
@NullMarked
public class StartScenarioFactory {

    /**
     * Обрабатывает команду /start.
     * <p>
     * Очищает предыдущие данные диалога, отображает приветственное сообщение
     * и клавиатуру с доступными командами.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с приветствием и клавиатурой
     */
    @TelegramScenarioAction
    public SendMessage startDialog(ActionContext<?> context) {
        ActionContextUtil.getVariables(context).clear();
        log.info("Старт диалога, chatId={}", ActionContextUtil.getChatId(context));
        return SendMessage.builder()
                .chatId(ActionContextUtil.getChatId(context))
                .text(MessageUtil.getWelcomeMessage(ActionContextUtil.getFirstName(context)))
                .replyMarkup(TelegramKeyboardUtil.createCommandsKeyboard())
                .build();
    }
}
