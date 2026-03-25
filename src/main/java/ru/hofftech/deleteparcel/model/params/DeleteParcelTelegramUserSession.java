package ru.hofftech.deleteparcel.model.params;

import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.deleteparcel.enums.DeleteParcelTelegramStep;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;

/**
 * Сессия пользователя для удаления посылки в Telegram.
 */
@Getter
@Builder
@With
// Рекорд не может быть создан с интерфейсом
@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public class DeleteParcelTelegramUserSession implements TelegramUserSession {
    private final long chatId;

    private final DeleteParcelTelegramStep step;

    @Nullable
    private final String name;

    /**
     * Создаёт начальную сессию для удаления посылки.
     *
     * @param chatId идентификатор чата
     * @return новая сессия
     */
    public static DeleteParcelTelegramUserSession start(long chatId) {
        return DeleteParcelTelegramUserSession.builder()
                .chatId(chatId)
                .step(DeleteParcelTelegramStep.ENTER_NAME)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public TelegramCommandType getCurrentCommand() {
        return TelegramCommandType.DELETE_PARCEL;
    }
}
