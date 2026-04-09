package ru.hofftech.core.mapper;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.hofftech.core.exception.ParcelException;
import ru.hofftech.core.model.core.Parcel;
import ru.hofftech.core.model.core.ParserParcelProcessorResult;
import ru.hofftech.core.model.entity.ParcelEntity;
import ru.hofftech.core.service.parcer.ParserParcelService;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.ParcelFormRequestDto;

import java.util.List;

/**
 * Маппер для преобразования между ParcelEntity и связанными DTO.
 * <p>
 * Использует ParserParcelService для сложного преобразования формы в grid.
 */
@NullMarked
@Component
@RequiredArgsConstructor
public class ParcelEntityMapper {
    private final ParserParcelService parserParcelProcessor;
    private final CoreMapper coreMapper;

    /**
     * Преобразует ParcelEntity в Parcel (для бизнес-логики).
     *
     * @param entity сущность посылки из БД
     * @return сущность посылки для бизнес-логики
     */
    public Parcel toParcel(ParcelEntity entity) {
        // Разбираем form в список строк
        List<String> lines = entity.getForm().lines().toList();

        return parserParcelProcessor.buildParcel(entity.getName(), lines, String.valueOf(entity.getSymbol()));
    }

    /**
     * Преобразует ParcelEntity в ParcelDto.
     *
     * @param entity сущность посылки из БД
     * @return DTO посылки
     */
    public ParcelDto toDto(ParcelEntity entity) {
        return coreMapper.parcelToParcelDto(toParcel(entity));
    }

    /**
     * Преобразует ParcelFormRequestDto в Parcel.
     *
     * @param parcelFormRequestDto DTO запроса на создание/обновление посылки
     * @return сущность посылки
     * @throws ParcelException если не удалось распознать посылку
     */
    public Parcel toParcel(ParcelFormRequestDto parcelFormRequestDto) {
        ParserParcelProcessorResult processorResult = parserParcelProcessor.transform(List.of(parcelFormRequestDto));

        if (processorResult.parcels().isEmpty()) {
            throw new ParcelException(
                    processorResult.getErrorsAsString().isBlank()
                            ? "Не удалось распознать посылку"
                            : processorResult.getErrorsAsString());
        }

        return processorResult.parcels().getFirst();
    }

    /**
     * Преобразует Parcel в ParcelEntity (создание или обновление).
     *
     * @param parcel сущность посылки для бизнес-логики
     * @param entity существующая сущность из БД (может быть null для создания)
     * @return сущность для сохранения в БД
     */
    public ParcelEntity toParcelEntity(Parcel parcel, @Nullable ParcelEntity entity) {
        if (entity == null) {
            return ParcelEntity.builder()
                    .name(parcel.name())
                    .form(parcel.getForm())
                    .symbol(parcel.symbol())
                    .build();
        }
        return entity.withName(parcel.name()).withForm(parcel.getForm()).withSymbol(parcel.symbol());
    }

    /**
     * Преобразует Parcel в ParcelEntity (создание новой).
     *
     * @param parcel сущность посылки для бизнес-логики
     * @return новая сущность для сохранения в БД
     */
    public ParcelEntity toParcelEntity(Parcel parcel) {
        return toParcelEntity(parcel, null);
    }
}
