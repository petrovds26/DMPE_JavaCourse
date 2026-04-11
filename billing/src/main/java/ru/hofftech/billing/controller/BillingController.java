package ru.hofftech.billing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hofftech.billing.service.BillingService;
import ru.hofftech.billing.util.ResponseWrapperUtil;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.PageDto;

import java.time.LocalDate;

/**
 * Контроллер для работы с биллингом.
 * <p>
 * Предоставляет REST API для получения истории платежей пользователей.
 */
@Tag(name = "Billing", description = "Контроллер для работы с биллингом")
@RestController
@RequestMapping(value = "${urls.app.core.root}/billing")
@RequiredArgsConstructor
@NullMarked
public class BillingController {

    private final BillingService billingService;

    /**
     * Получает историю биллинга для пользователя за указанный период с пагинацией.
     *
     * @param userId идентификатор пользователя
     * @param from   дата начала периода (опционально, формат dd.MM.yyyy)
     * @param to     дата окончания периода (опционально, формат dd.MM.yyyy)
     * @param page   номер страницы для пагинации (начиная с 0), по умолчанию 0
     * @param size   размер страницы, по умолчанию 20
     * @return ответ со списком записей биллинга
     */
    @GetMapping("/history")
    @Operation(summary = "Получить историю биллинга для пользователя с пагинацией")
    public ResponseEntity<Response<PageDto<BillingDto>>> readBillingHistory(
            @RequestParam @Valid String userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate to,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size) {

        return ResponseWrapperUtil.ok(billingService.requestBillingHistory(userId, from, to, page, size));
    }
}
