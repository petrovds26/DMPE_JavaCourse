package ru.hofftech.load.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.load.model.core.LoadResult;
import ru.hofftech.load.model.core.LoadStrategyParcelInvalid;
import ru.hofftech.load.model.dto.LoadParcelInvalidDto;
import ru.hofftech.load.model.dto.LoadParcelOutputResultDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.util.MapperUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Утилитный класс для преобразования между сущностями и DTO в модуле загрузки.
 */
@UtilityClass
@NullMarked
public class LoadParcelMapperUtil {

    /**
     * Преобразует LoadStrategyParcelInvalid в LoadParcelInvalidDto.
     *
     * @param importParcelInvalid объект с информацией об ошибке (может быть null)
     * @return DTO с информацией об ошибке или null, если входной параметр null
     */
    @Nullable
    public LoadParcelInvalidDto invalidParcelToDto(@Nullable LoadStrategyParcelInvalid importParcelInvalid) {
        if (importParcelInvalid == null) {
            return null;
        }

        ParcelDto parcelDto = MapperUtil.parcelToDto(importParcelInvalid.parcel());

        if (parcelDto == null) {
            return null;
        }

        return LoadParcelInvalidDto.builder()
                .parcel(parcelDto)
                .cause(importParcelInvalid.cause())
                .build();
    }

    /**
     * Преобразует список LoadStrategyParcelInvalid в список DTO.
     *
     * @param importParcelInvalids список с информацией об ошибках (может быть null)
     * @return список DTO (не может быть null)
     */
    public List<LoadParcelInvalidDto> invalidParcelsToDto(
            @Nullable List<LoadStrategyParcelInvalid> importParcelInvalids) {
        if (importParcelInvalids == null) {
            return List.of();
        }
        return importParcelInvalids.stream()
                .map(LoadParcelMapperUtil::invalidParcelToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    /**
     * Преобразует LoadResult в LoadParcelOutputResultDto.
     *
     * @param result результат загрузки (может быть null)
     * @return DTO с результатом или null, если входной параметр null
     */
    @Nullable
    public LoadParcelOutputResultDto loadingResultToOutputDto(@Nullable LoadResult result) {
        if (result == null) {
            return null;
        }

        return LoadParcelOutputResultDto.builder()
                .machines(MapperUtil.machinesToDto(result.machines()))
                .invalidParcels(invalidParcelsToDto(result.loadStrategyParcelInvalids()))
                .errors(result.errors())
                .build();
    }
}
