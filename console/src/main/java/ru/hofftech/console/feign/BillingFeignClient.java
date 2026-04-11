package ru.hofftech.console.feign;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.LoadRequestDto;
import ru.hofftech.shared.model.dto.LoadResponseDto;
import ru.hofftech.shared.model.dto.PageDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.ParcelFormRequestDto;
import ru.hofftech.shared.model.dto.ParcelNameRequestDto;
import ru.hofftech.shared.model.dto.UnloadRequestDto;
import ru.hofftech.shared.model.dto.UnloadResponseDto;

import java.util.List;

/**
 * Feign клиент для взаимодействия с Billing модулем.
 * <p>
 * Предоставляет методы для вызова REST API сервиса биллинга:
 * получение истории платежей пользователей с пагинацией.
 */
@NullMarked
@FeignClient(name = "BillingFeignClient", url = "${billing.api.url}")
public interface BillingFeignClient {

    /**
     * Получает историю биллинга для пользователя с пагинацией.
     * <p>
     * Позволяет фильтровать записи по периоду и использовать пагинацию
     * для удобного просмотра большого количества записей.
     *
     * @param userId идентификатор пользователя (обязательный)
     * @param from   дата начала периода (опционально, формат dd.MM.yyyy).
     *               Если не указана, выводятся записи с начала времён.
     * @param to     дата окончания периода (опционально, формат dd.MM.yyyy).
     *               Если не указана, выводятся записи до текущего момента.
     * @param page   номер страницы для пагинации (начиная с 0)
     * @return ответ сервера с пагинированным списком записей биллинга
     */
    @GetMapping("/billing/v1/billing/history")
    Response<PageDto<BillingDto>> readBilling(
            @RequestParam("userId") String userId,
            @RequestParam(value = "from", required = false) @Nullable String from,
            @RequestParam(value = "to", required = false) @Nullable String to,
            @RequestParam("page") Integer page);
}
