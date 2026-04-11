package ru.hofftech.console.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.hofftech.console.exception.FeignException;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.PageDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.ParcelFormRequestDto;
import ru.hofftech.shared.model.dto.ParcelNameRequestDto;
import ru.hofftech.shared.util.PrintStringUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для CRUD операций с посылками в консольном приложении.
 * <p>
 * Предоставляет методы для создания, чтения, обновления и удаления посылок
 * через взаимодействие с Core сервисом.
 */
@NullMarked
@Service
@RequiredArgsConstructor
public class ParcelService {
    private final ExternalService externalService;

    /**
     * Создаёт новую посылку.
     *
     * @param name   название посылки
     * @param form   форма посылки
     * @param symbol символ посылки
     * @return сообщение о результате операции
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    public String createParcel(String name, String form, String symbol) {
        Response<String> response = externalService.createParcel(ParcelFormRequestDto.builder()
                .name(name)
                .form(form)
                .symbol(symbol)
                .build());

        if (response.isSuccess()) {
            return response.getData();
        }

        throw new FeignException(response);
    }

    /**
     * Обновляет существующую посылку.
     *
     * @param name   название посылки
     * @param form   новая форма посылки
     * @param symbol новый символ посылки
     * @return сообщение о результате операции
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    public String updateParcel(String name, String form, String symbol) {
        Response<String> response = externalService.updateParcel(ParcelFormRequestDto.builder()
                .name(name)
                .form(form)
                .symbol(symbol)
                .build());

        if (response.isSuccess()) {
            return response.getData();
        }

        throw new FeignException(response);
    }

    /**
     * Удаляет посылку по названию.
     *
     * @param name название посылки
     * @return сообщение о результате операции
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    public String deleteParcel(String name) {
        Response<String> response = externalService.deleteParcel(
                ParcelNameRequestDto.builder().name(name).build());

        if (response.isSuccess()) {
            return response.getData();
        }

        throw new FeignException(response);
    }

    /**
     * Показывает информацию о посылке по названию.
     *
     * @param name название посылки
     * @return отформатированная информация о посылке
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    public String readParcel(String name) {
        Response<List<ParcelDto>> response = externalService.readParcelByName(name);

        if (response.isSuccess()) {
            return PrintStringUtil.parcelRender(response.getData().getFirst());
        }

        throw new FeignException(response);
    }

    /**
     * Показывает список всех посылок.
     *
     * @return отформатированный список всех посылок
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    public String readAllParcels(Integer page) {
        Response<PageDto<ParcelDto>> response = externalService.readAllParcels(page);

        if (response.isSuccess()) {
            return "Список посылок:\n"
                    + response.getData().content().stream()
                            .map(PrintStringUtil::parcelRender)
                            .collect(Collectors.joining("\n"));
        }

        throw new FeignException(response);
    }
}
