package ru.hofftech.console.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.hofftech.console.feign.CoreFeignClient;
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
import ru.hofftech.shared.model.enums.ResponseCode;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Сервис для взаимодействия с Core модулем через Feign клиент.
 * <p>
 * Предоставляет методы для выполнения операций с посылками,
 * загрузкой, разгрузкой и биллингом. Все методы обёрнуты
 * в единый механизм обработки ошибок.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@NullMarked
public class CoreService {

    private final CoreFeignClient coreFeignClient;

    /**
     * Создаёт новую посылку.
     *
     * @param request DTO с данными для создания посылки
     * @return ответ сервера с результатом операции
     */
    public Response<String> createParcel(ParcelFormRequestDto request) {
        return executeRequest(() -> coreFeignClient.createParcel(request));
    }

    /**
     * Получает список всех посылок.
     * @param page     номер страницы для пагинации (опционально)
     * @return ответ сервера со списком DTO посылок
     */
    public Response<PageDto<ParcelDto>> readAllParcels(Integer page) {
        return executeRequest(() -> coreFeignClient.readAllParcels(page));
    }

    /**
     * Получает посылку по названию.
     *
     * @param name название посылки
     * @return ответ сервера с DTO посылки
     */
    public Response<List<ParcelDto>> readParcelByName(String name) {
        return executeRequest(() -> coreFeignClient.readParcelByName(name));
    }

    /**
     * Обновляет существующую посылку.
     *
     * @param request DTO с обновлёнными данными посылки
     * @return ответ сервера с результатом операции
     */
    public Response<String> updateParcel(ParcelFormRequestDto request) {
        return executeRequest(() -> coreFeignClient.updateParcel(request));
    }

    /**
     * Удаляет посылку по названию.
     *
     * @param request DTO с названием посылки
     * @return ответ сервера с результатом операции
     */
    public Response<String> deleteParcel(ParcelNameRequestDto request) {
        return executeRequest(() -> coreFeignClient.deleteParcel(request));
    }

    /**
     * Выполняет загрузку посылок в машины.
     *
     * @param request DTO с параметрами загрузки
     * @return ответ сервера с результатом загрузки
     */
    public Response<LoadResponseDto> loadParcel(LoadRequestDto request) {
        return executeRequest(() -> coreFeignClient.loadParcel(request));
    }

    /**
     * Выполняет разгрузку машин.
     *
     * @param request DTO с параметрами разгрузки
     * @return ответ сервера с результатом разгрузки
     */
    public Response<UnloadResponseDto> unloadParcel(UnloadRequestDto request) {
        return executeRequest(() -> coreFeignClient.unloadParcel(request));
    }

    /**
     * Получает историю биллинга для пользователя.
     *
     * @param userId      идентификатор пользователя
     * @param fromDateStr дата начала периода (опционально)
     * @param toDateStr   дата окончания периода (опционально)
     * @param page        номер страницы для пагинации (опционально)
     * @return ответ сервера со списком записей биллинга
     */
    public Response<PageDto<BillingDto>> readBilling(
            String userId, @Nullable String fromDateStr, @Nullable String toDateStr, Integer page) {
        return executeRequest(() -> coreFeignClient.readBilling(userId, fromDateStr, toDateStr, page));
    }

    /**
     * Универсальный метод для выполнения запросов к Core сервису с обработкой ошибок.
     *
     * @param request функция, выполняющая запрос
     * @param <T>     тип данных ответа
     * @return ответ сервера, обёрнутый в Response
     */
    private <T> Response<T> executeRequest(Callable<Response<T>> request) {
        try {
            return request.call();
        } catch (FeignException e) {
            log.error("Feign exception: {}", e.getMessage(), e);
            return handleFeignException(e);
        } catch (Exception e) {
            log.error("Unexpected exception: {}", e.getMessage(), e);
            return Response.error(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Обрабатывает Feign исключения и преобразует их в Response с ошибкой.
     *
     * @param e исключение Feign
     * @param <T> тип данных ответа
     * @return Response с кодом ошибки
     */
    private <T> Response<T> handleFeignException(FeignException e) {
        // Проверка на таймаут
        if (e.getCause() instanceof java.net.SocketTimeoutException) {
            return Response.error(ResponseCode.REQUEST_TIMEOUT, e.getMessage());
        }
        return Response.error(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
