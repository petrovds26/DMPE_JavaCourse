package ru.hofftech.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hofftech.core.service.ParcelService;
import ru.hofftech.core.util.ResponseWrapperUtil;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.newdto.ParcelFormRequestDto;
import ru.hofftech.shared.model.dto.newdto.ParcelNameRequestDto;

import java.util.List;

/**
 * Контроллер для CRUD операций с посылками.
 * <p>
 * Предоставляет REST API для создания, чтения, обновления и удаления посылок.
 */
@Tag(name = "Parcel", description = "Контроллер для CRUD операций с посылками")
@RestController
@Validated
@RequestMapping(value = "${urls.app.core.root}/parcel")
@RequiredArgsConstructor
@NullMarked
public class ParcelController {
    private final ParcelService parcelService;

    /**
     * Создаёт новую посылку.
     *
     * @param parcelFormRequestDto DTO с данными посылки
     * @return ответ с результатом операции
     */
    @PutMapping("/create")
    @Operation(summary = "Метод создания посылки")
    public ResponseEntity<Response<String>> createParcel(
            @Valid @RequestBody ParcelFormRequestDto parcelFormRequestDto) {

        return ResponseWrapperUtil.ok(parcelService.create(parcelFormRequestDto));
    }

    /**
     * Обновляет существующую посылку.
     *
     * @param parcelFormDto DTO с обновлёнными данными посылки
     * @return ответ с результатом операции
     */
    @PutMapping("/update")
    @Operation(summary = "Метод обновления посылки")
    public ResponseEntity<Response<String>> updateParcel(@Valid @RequestBody ParcelFormRequestDto parcelFormDto) {

        return ResponseWrapperUtil.ok(parcelService.update(parcelFormDto));
    }

    /**
     * Удаляет посылку по названию.
     *
     * @param parcelNameRequestDto DTO с названием посылки
     * @return ответ с результатом операции
     */
    @DeleteMapping("/delete")
    @Operation(summary = "Метод удаления посылок")
    public ResponseEntity<Response<String>> updateParcel(
            @Valid @RequestBody ParcelNameRequestDto parcelNameRequestDto) {

        return ResponseWrapperUtil.ok(parcelService.delete(parcelNameRequestDto));
    }

    /**
     * Чтение посылок.
     * <p>
     * Без параметра name возвращает все посылки.
     * С параметром name возвращает конкретную посылку по названию.
     *
     * @param name название посылки (опционально)
     * @return ответ со списком посылок
     */
    @GetMapping(value = {"/read", "/read/{name}"})
    @Operation(summary = "Метод чтения посылок")
    public ResponseEntity<Response<List<ParcelDto>>> readParcel(@PathVariable(required = false) @Nullable String name) {

        if (name == null || name.isBlank()) {
            return ResponseWrapperUtil.ok(parcelService.readAll());
        }
        return ResponseWrapperUtil.ok(parcelService.readByName(name));
    }
}
