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
import ru.hofftech.core.service.UnloadService;
import ru.hofftech.core.util.ResponseWrapperUtil;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.newdto.UnloadRequestDto;
import ru.hofftech.shared.model.dto.newdto.UnloadResponseDto;

/**
 * Контроллер для разгрузки машин.
 * <p>
 * Предоставляет REST API для выполнения операций разгрузки
 * посылок из машин.
 */
@Tag(name = "Unload", description = "Контроллер для разгрузки машин")
@RestController
@Validated
@RequestMapping(value = "${urls.app.core.root}/unload")
@RequiredArgsConstructor
@NullMarked
public class UnloadController {

    private final UnloadService unloadService;

    /**
     * Выполняет разгрузку машин.
     *
     * @param unloadRequestDto DTO с параметрами разгрузки
     * @return ответ с результатами разгрузки
     */
    @PostMapping
    @Operation(summary = "Метод разгрузки машин")
    public ResponseEntity<Response<UnloadResponseDto>> unloadParcels(
            @Valid @RequestBody UnloadRequestDto unloadRequestDto) {

        return ResponseWrapperUtil.ok(unloadService.unload(unloadRequestDto));
    }
}
