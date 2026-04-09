package ru.hofftech.console.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.hofftech.console.exception.FeignException;
import ru.hofftech.console.exception.ValidateException;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.PageDto;
import ru.hofftech.shared.util.PrintStringUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Сервис для работы с биллингом в консольном приложении.
 * <p>
 * Предоставляет методы для получения истории биллинга
 * и форматирования вывода для консоли.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@NullMarked
public class BillingService {
    private final CoreService coreService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Получает историю биллинга для пользователя и форматирует для вывода в консоль.
     *
     * @param userId      идентификатор пользователя
     * @param fromDateStr дата начала периода (опционально, формат dd.MM.yyyy)
     * @param toDateStr   дата окончания периода (опционально, формат dd.MM.yyyy)
     * @param page        номер страницы для пагинации (опционально, начиная с 0).
     * @return отформатированная история биллинга
     * @throws ValidateException если формат даты не соответствует ожидаемому
     * @throws FeignException    если при вызове Core сервиса произошла ошибка
     */
    public String readBilling(String userId, @Nullable String fromDateStr, @Nullable String toDateStr, Integer page) {
        validDate(fromDateStr);
        validDate(toDateStr);

        Response<PageDto<BillingDto>> response = coreService.readBilling(userId, fromDateStr, toDateStr, page);

        if (response.isSuccess()) {
            return PrintStringUtil.formatBillingHistory(response.getData().content());
        }

        throw new FeignException(response);
    }

    /**
     * Валидирует дату в формате dd.MM.yyyy.
     * <p>
     * Если дата не указана (null или пустая строка), валидация считается успешной.
     *
     * @param dateStr строка с датой
     * @throws ValidateException если дата указана, но имеет неверный формат
     */
    private void validDate(@Nullable String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return;
        }

        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            throw new ValidateException(
                    String.format("Неверный формат даты: %s. Ожидается формат dd.MM.yyyy", dateStr));
        }
    }
}
