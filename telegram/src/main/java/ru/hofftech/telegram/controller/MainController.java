package ru.hofftech.telegram.controller;

import io.github.drednote.telegram.core.annotation.TelegramController;
import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import ru.hofftech.telegram.util.MessageUtil;

/**
 * Основной контроллер для обработки запросов к Telegram боту.
 * <p>
 * Обрабатывает все запросы, которые не были обработаны специализированными сценариями.
 */
@Slf4j
@Component
@TelegramController
@RequiredArgsConstructor
@NullMarked
public class MainController {

    /**
     * Обрабатывает все запросы, не попавшие под другие маппинги.
     *
     * @return ответ с сообщением о неизвестной команде
     */
    @TelegramRequest
    public TelegramResponse onAll() {
        return new GenericTelegramResponse(MessageUtil.getUnknownCommandMessage());
    }
}
