package ru.hofftech.load.model.params;

import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.load.model.enums.LoadStrategyType;
import ru.hofftech.load.model.enums.LoadTelegramStep;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.enums.TelegramCommandType;
import ru.hofftech.shared.model.params.TelegramUserSession;

import java.util.List;

/**
 * Сессия пользователя для создания посылки в Telegram.
 * Хранит состояние процесса и введённые данные.
 */
@Getter
@Builder
@With
// Рекорд не может быть создан с интерфейсом
@SuppressWarnings("ClassCanBeRecord")
public class LoadTelegramUserSession implements TelegramUserSession {
    private final long chatId;

    @NonNull
    private final LoadTelegramStep step;

    @Nullable
    private final List<Parcel> parcels;

    @Nullable
    private final List<Machine> machines;

    @Nullable
    private final LoadStrategyType strategyType;

    /**
     * Создаёт начальную сессию для создания посылки.
     *
     * @param chatId идентификатор чата
     * @return новая сессия
     */
    @NonNull
    public static LoadTelegramUserSession start(long chatId) {
        return LoadTelegramUserSession.builder()
                .chatId(chatId)
                .step(LoadTelegramStep.ENTER_PARCEL)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public TelegramCommandType getCurrentCommand() {
        return TelegramCommandType.LOAD;
    }
}
