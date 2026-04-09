package ru.hofftech.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hofftech.core.service.LoadService;
import ru.hofftech.core.util.ResponseWrapperUtil;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.LoadRequestDto;
import ru.hofftech.shared.model.dto.LoadResponseDto;

/**
 * Контроллер для загрузки посылок в машины.
 * <p>
 * Предоставляет REST API для выполнения операций загрузки
 * посылок в машины с использованием различных стратегий.
 */
@Tag(name = "Load", description = "Контроллер для загрузки посылок в машины")
@RestController
@Validated
@RequestMapping(value = "${urls.app.core.root}/load")
@RequiredArgsConstructor
@NullMarked
public class LoadController {

    private final LoadService loadService;

    /**
     * Выполняет загрузку посылок в машины.
     *
     * @param loadRequestDto DTO с параметрами загрузки
     * @return ответ с результатами загрузки
     */
    @PostMapping
    @Operation(summary = "Метод загрузки посылок в машины")
    public ResponseEntity<Response<LoadResponseDto>> loadParcels(@Valid @RequestBody LoadRequestDto loadRequestDto) {

        return ResponseWrapperUtil.ok(loadService.load(loadRequestDto));
    }
}
