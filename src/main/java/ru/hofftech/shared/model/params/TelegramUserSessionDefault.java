package ru.hofftech.shared.model.params;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.enums.TelegramCommandType;

@NullMarked
public class TelegramUserSessionDefault implements TelegramUserSession {
    @Override
    public @Nullable TelegramCommandType getCurrentCommand() {
        return null;
    }
}
