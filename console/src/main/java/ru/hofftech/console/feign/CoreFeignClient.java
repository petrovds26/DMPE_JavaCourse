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
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.newdto.BillingDto;
import ru.hofftech.shared.model.dto.newdto.LoadRequestDto;
import ru.hofftech.shared.model.dto.newdto.LoadResponseDto;
import ru.hofftech.shared.model.dto.newdto.ParcelFormRequestDto;
import ru.hofftech.shared.model.dto.newdto.ParcelNameRequestDto;
import ru.hofftech.shared.model.dto.newdto.UnloadRequestDto;
import ru.hofftech.shared.model.dto.newdto.UnloadResponseDto;

import java.util.List;

/**
 * Feign клиент для взаимодействия с Core модулем.
 * <p>
 * Предоставляет методы для вызова REST API основного сервиса:
 * CRUD операций с посылками, загрузки/разгрузки машин и биллинга.
 */
@NullMarked
@FeignClient(name = "CoreFeignClient", url = "${core.api.url}")
public interface CoreFeignClient {

    /**
     * Создаёт новую посылку.
     *
     * @param parcelFormRequestDto DTO с данными посылки
     * @return ответ сервера с результатом операции
     */
    @PutMapping("/core/v1/parcel/create")
    Response<String> createParcel(@RequestBody ParcelFormRequestDto parcelFormRequestDto);

    /**
     * Получает список всех посылок.
     *
     * @return ответ сервера со списком DTO посылок
     */
    @GetMapping("/core/v1/parcel/read")
    Response<List<ParcelDto>> readAllParcels();

    /**
     * Получает посылку по названию.
     *
     * @param name название посылки
     * @return ответ сервера с DTO посылки
     */
    @GetMapping("/core/v1/parcel/read/{name}")
    Response<List<ParcelDto>> readParcelByName(@PathVariable("name") String name);

    /**
     * Обновляет существующую посылку.
     *
     * @param parcelFormRequestDto DTO с обновлёнными данными
     * @return ответ сервера с результатом операции
     */
    @PutMapping("/core/v1/parcel/update")
    Response<String> updateParcel(@RequestBody ParcelFormRequestDto parcelFormRequestDto);

    /**
     * Удаляет посылку по названию.
     *
     * @param parcelNameRequestDto DTO с названием посылки
     * @return ответ сервера с результатом операции
     */
    @DeleteMapping("/core/v1/parcel/delete")
    Response<String> deleteParcel(@RequestBody ParcelNameRequestDto parcelNameRequestDto);

    /**
     * Выполняет загрузку посылок в машины.
     *
     * @param loadRequestDto DTO с параметрами загрузки
     * @return ответ сервера с результатом загрузки
     */
    @PostMapping("/core/v1/load")
    Response<LoadResponseDto> loadParcel(@RequestBody LoadRequestDto loadRequestDto);

    /**
     * Выполняет разгрузку машин.
     *
     * @param loadRequestDto DTO с параметрами разгрузки
     * @return ответ сервера с результатом разгрузки
     */
    @PostMapping("/core/v1/unload")
    Response<UnloadResponseDto> unloadParcel(@RequestBody UnloadRequestDto loadRequestDto);

    /**
     * Получает историю биллинга для пользователя.
     *
     * @param userId идентификатор пользователя
     * @param from   дата начала периода (опционально, формат dd.MM.yyyy)
     * @param to     дата окончания периода (опционально, формат dd.MM.yyyy)
     * @return ответ сервера со списком записей биллинга
     */
    @GetMapping("/core/v1/billing/history")
    Response<List<BillingDto>> readBilling(
            @RequestParam("userId") String userId,
            @RequestParam(value = "from", required = false) @Nullable String from,
            @RequestParam(value = "to", required = false) @Nullable String to);
}
