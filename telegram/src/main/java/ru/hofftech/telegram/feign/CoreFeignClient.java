package ru.hofftech.telegram.feign;

import org.jspecify.annotations.NullMarked;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.LoadRequestDto;
import ru.hofftech.shared.model.dto.LoadResponseDto;
import ru.hofftech.shared.model.dto.PageDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.ParcelFormRequestDto;
import ru.hofftech.shared.model.dto.ParcelNameRequestDto;

import java.util.List;

/**
 * Feign клиент для взаимодействия с API Яндекс Доставки.
 * <p>
 * Предоставляет методы для работы с пунктами выдачи заказов (ПВЗ),
 * создания заявок на доставку и их подтверждения.
 */
@NullMarked
@FeignClient(name = "CoreFeignClient", url = "${core.api.url}")
public interface CoreFeignClient {

    /**
     * Создание новой посылки
     */
    @PutMapping("/core/v1/parcel/create")
    Response<String> createParcel(@RequestBody ParcelFormRequestDto parcelFormRequestDto);

    /**
     * Получение всех посылок
     */
    @GetMapping("/core/v1/parcel/read")
    Response<PageDto<ParcelDto>> readAllParcels();

    /**
     * Получение посылки по названию
     */
    @GetMapping("/core/v1/parcel/read/{name}")
    Response<List<ParcelDto>> readParcelByName(@PathVariable("name") String name);

    /**
     * Обновление посылки
     */
    @PutMapping("/core/v1/parcel/update")
    Response<String> updateParcel(@RequestBody ParcelFormRequestDto parcelFormRequestDto);

    /**
     * Удаление посылки
     */
    @DeleteMapping("/core/v1/parcel/delete")
    Response<String> deleteParcel(@RequestBody ParcelNameRequestDto parcelNameRequestDto);

    /**
     * Загрузка посылок
     */
    @PostMapping("/core/v1/load")
    Response<LoadResponseDto> loadParcel(@RequestBody LoadRequestDto loadRequestDto);
}
