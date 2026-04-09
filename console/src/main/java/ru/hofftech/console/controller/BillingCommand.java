package ru.hofftech.console.controller;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;
import ru.hofftech.console.service.BillingService;

/**
 * Консольная команда для просмотра истории биллинга.
 */
@NullMarked
@RequiredArgsConstructor
@Component
@Command(command = "billing", description = "Просмотр истории биллинга")
public class BillingCommand extends BaseCommand {

    private final BillingService billingService;

    /**
     * Показывает историю биллинга для указанного пользователя.
     *
     * @param userId   идентификатор пользователя
     * @param fromDate дата начала периода (опционально, формат dd.MM.yyyy)
     * @param toDate   дата окончания периода (опционально, формат dd.MM.yyyy)
     * @return отформатированная история биллинга
     */
    @Command(command = "", description = "Показать историю биллинга")
    public String getBillingHistory(
            @Option(longNames = "userId", shortNames = 'u', description = "ID пользователя", required = true)
                    String userId,
            @Option(longNames = "from", shortNames = 'f', description = "Дата начала (dd.MM.yyyy)") @Nullable
                    String fromDate,
            @Option(longNames = "to", shortNames = 't', description = "Дата окончания (dd.MM.yyyy)") @Nullable
                    String toDate) {
        return executeWithErrorHandling(() -> billingService.readBilling(userId, fromDate, toDate));
    }
}
