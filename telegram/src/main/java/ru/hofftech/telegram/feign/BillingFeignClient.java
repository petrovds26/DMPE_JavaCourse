package ru.hofftech.telegram.feign;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.PageDto;

/**
 * Feign клиент для взаимодействия с Billing модулем.
 * <p>
 * Предоставляет методы для вызова REST API сервиса биллинга:
 * получение истории платежей пользователей.
 */
@NullMarked
@FeignClient(name = "BillingFeignClient", url = "${billing.api.url}")
public interface BillingFeignClient {

    /**
     * Запрос истории оплаты
     */
    @GetMapping("/billing/v1/billing/history")
    Response<PageDto<BillingDto>> readBilling(
            @RequestParam("userId") String userId,
            @RequestParam(value = "from", required = false) @Nullable String from,
            @RequestParam(value = "to", required = false) @Nullable String to);
}
