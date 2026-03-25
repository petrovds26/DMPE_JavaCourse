package ru.hofftech.updateparcel.model.params;

import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;
import ru.hofftech.updateparcel.enums.UpdateParcelTelegramStep;

/**
 * Сессия пользователя для обновления посылки в Telegram.
 */
@Getter
@Builder
@With
@NullMarked
// Рекорд не может быть создан с интерфейсом
@SuppressWarnings("ClassCanBeRecord")
public class UpdateParcelTelegramUserSession implements TelegramUserSession {
    private final long chatId;

    private final UpdateParcelTelegramStep step;

    @Nullable
    private final String name;

    @Nullable
    private final String form;

    @Nullable
    private final String symbol;

    /**
     * Создаёт начальную сессию для обновления посылки.
     *
     * @param chatId идентификатор чата
     * @return новая сессия
     */
    public static UpdateParcelTelegramUserSession start(long chatId) {
        return UpdateParcelTelegramUserSession.builder()
                .chatId(chatId)
                .step(UpdateParcelTelegramStep.ENTER_NAME)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public TelegramCommandType getCurrentCommand() {
        return TelegramCommandType.UPDATE_PARCEL;
    }
}
