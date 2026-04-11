package ru.hofftech.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hofftech.core.schedule.BillingOutboxScheduler;
import ru.hofftech.core.util.ResponseWrapperUtil;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.ManualSchedulerResponseDto;

/**
 * Контроллер для ручного управления периодическими задачами.
 * <p>
 * Предоставляет REST API для получения статуса и ручного запуска
 * задач планировщика (например, отправки данных в сервис биллинга).
 */
@Tag(name = "Scheduler", description = "Ручной запуск периодических задач")
@RestController
@Validated
@RequestMapping(value = "${urls.app.core.root}/scheduler")
@RequiredArgsConstructor
@NullMarked
public class ManualSchedulerController {
    private final BillingOutboxScheduler billingOutboxScheduler;

    /**
     * Возвращает статус шедуллера отправки данных в сервис биллинга.
     *
     * @return статус задачи (заблокирована/не заблокирована, время последнего запуска)
     */
    @GetMapping("/billing-outbox/status")
    @Operation(summary = "Метод возвращает статус шедуллера отправки данных в сервис Биллинг")
    public ResponseEntity<Response<ManualSchedulerResponseDto>> scheduleStatus() {

        ManualSchedulerResponseDto manualResponseDto = billingOutboxScheduler.receiveStatus();
        return ResponseWrapperUtil.ok(manualResponseDto);
    }

    /**
     * Запускает периодическую задачу отправки данных в сервис биллинг вручную.
     *
     * @return статус запуска задачи
     */
    @GetMapping("/billing-outbox/execute")
    @Operation(summary = "Метод запускает периодическую задачу отправки данных в сервис Биллинг")
    public ResponseEntity<Response<ManualSchedulerResponseDto>> scheduleExecute() {

        ManualSchedulerResponseDto manualResponseDto = billingOutboxScheduler.executeScheduler();
        return ResponseWrapperUtil.ok(manualResponseDto);
    }
}
