package ru.hofftech.createparcel.model.params;

import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.createparcel.enums.CreateParcelTelegramStep;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;

/**
 * Сессия пользователя для создания посылки в Telegram.
 * Хранит состояние процесса и введённые данные.
 */
@Getter
@Builder
@With
// Рекорд не может быть создан с интерфейсом
@SuppressWarnings("ClassCanBeRecord")
public class CreateParcelTelegramUserSession implements TelegramUserSession {
    private final long chatId;

    @NonNull
    private final CreateParcelTelegramStep step;

    @Nullable
    private final String name;

    @Nullable
    private final String form;

    @Nullable
    private final String symbol;

    /**
     * Создаёт начальную сессию для создания посылки.
     *
     * @param chatId идентификатор чата
     * @return новая сессия
     */
    public static CreateParcelTelegramUserSession start(long chatId) {
        return CreateParcelTelegramUserSession.builder()
                .chatId(chatId)
                .step(CreateParcelTelegramStep.ENTER_NAME)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public TelegramCommandType getCurrentCommand() {
        return TelegramCommandType.CREATE_PARCEL;
    }
}
