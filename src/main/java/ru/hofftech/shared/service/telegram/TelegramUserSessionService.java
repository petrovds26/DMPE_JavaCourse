package ru.hofftech.shared.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.params.TelegramUserSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для управления сессиями пользователей Telegram.
 * Хранит сессии в памяти и предоставляет методы для их получения, обновления и очистки.
 */
@Slf4j
public class TelegramUserSessionService {
    private final Map<Long, TelegramUserSession> sessions = new ConcurrentHashMap<>();

    /**
     * Получить сессию пользователя по ID чата.
     *
     * @param chatId идентификатор чата
     * @return сессия пользователя или null, если не найдена
     */
    @Nullable
    public TelegramUserSession getSession(long chatId) {
        return sessions.get(chatId);
    }

    /**
     * Создать или обновить сессию пользователя.
     *
     * @param chatId  идентификатор чата
     * @param session новая сессия (не может быть null)
     */
    public void createOrUpdateSession(long chatId, @NonNull TelegramUserSession session) {
        sessions.put(chatId, session);
        log.debug(
                "Сессия пользователя {} обновлена: {}",
                chatId,
                session.getClass().getSimpleName());
    }

    /**
     * Очистить сессию пользователя.
     *
     * @param chatId идентификатор чата
     */
    public void clearSession(long chatId) {
        sessions.remove(chatId);
        log.info("Сессия пользователя {} очищена", chatId);
    }
}
