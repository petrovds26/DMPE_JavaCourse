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
     * @param page     номер страницы для пагинации (опционально, начиная с 0).
     *                 По умолчанию 0 (первая страница).
     * @return отформатированная история биллинга
     */
    @Command(command = "", description = "Показать историю биллинга")
    public String getBillingHistory(
            @Option(longNames = "userId", shortNames = 'u', description = "ID пользователя", required = true)
                    String userId,
            @Option(longNames = "from", shortNames = 'f', description = "Дата начала (dd.MM.yyyy)") @Nullable
                    String fromDate,
            @Option(longNames = "to", shortNames = 't', description = "Дата окончания (dd.MM.yyyy)") @Nullable
                    String toDate,
            @Option(
                            longNames = "page",
                            shortNames = 'p',
                            description = "Номер страницы (начиная с 0)",
                            defaultValue = "0")
                    Integer page) {
        return executeWithErrorHandling(() -> billingService.readBilling(userId, fromDate, toDate, page));
    }
}
