package ru.hofftech.readparcel.model.params;

import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.readparcel.enums.ReadParcelTelegramStep;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;

/**
 * Сессия пользователя для чтения посылки в Telegram.
 */
@Getter
@Builder
@With
// Рекорд не может быть создан с интерфейсом
@SuppressWarnings("ClassCanBeRecord")
public class ReadParcelTelegramUserSession implements TelegramUserSession {
    private final long chatId;

    @NonNull
    private final ReadParcelTelegramStep step;

    @Nullable
    private final String name;

    /**
     * Создаёт начальную сессию для чтения посылки.
     *
     * @param chatId идентификатор чата
     * @return новая сессия
     */
    @NonNull
    public static ReadParcelTelegramUserSession start(long chatId) {
        return ReadParcelTelegramUserSession.builder()
                .chatId(chatId)
                .step(ReadParcelTelegramStep.ENTER_NAME)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public TelegramCommandType getCurrentCommand() {
        return TelegramCommandType.READ_PARCEL;
    }
}
