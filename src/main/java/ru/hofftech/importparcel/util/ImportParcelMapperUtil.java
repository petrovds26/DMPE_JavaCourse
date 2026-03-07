package ru.hofftech.importparcel.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importparcel.model.core.ImportParcelInvalid;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.model.dto.ImportParcelInvalidDto;
import ru.hofftech.importparcel.model.dto.ImportParcelOutputResultDto;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.util.MapperUtil;

import java.util.List;

@UtilityClass
public class ImportParcelMapperUtil {

    /**
     * Преобразует ImportParcelInvalid в ImportParcelInvalidDto
     */
    @Nullable
    public ImportParcelInvalidDto invalidParcelToDto(@Nullable ImportParcelInvalid importParcelInvalid) {
        if (importParcelInvalid == null) {
            return null;
        }

        ParcelDto parcelDto = MapperUtil.parcelToDto(importParcelInvalid.parcel());

        if (parcelDto == null) {
            return null;
        }

        return ImportParcelInvalidDto.builder()
                .parcel(parcelDto)
                .cause(importParcelInvalid.cause())
                .build();
    }

    /**
     * Преобразует список ImportParcelInvalid в список DTO
     */
    @NonNull
    public List<ImportParcelInvalidDto> invalidParcelsToDto(@Nullable List<ImportParcelInvalid> importParcelInvalids) {
        if (importParcelInvalids == null) {
            return List.of();
        }
        return importParcelInvalids.stream()
                .map(ImportParcelMapperUtil::invalidParcelToDto)
                .toList();
    }

    /**
     * Преобразует ImportParcelResult в ImportParcelOutputResultDto
     */
    @Nullable
    public ImportParcelOutputResultDto loadingResultToOutputDto(@Nullable ImportParcelResult result) {
        if (result == null) {
            return null;
        }

        return ImportParcelOutputResultDto.builder()
                .machines(MapperUtil.machinesToDto(result.machines()))
                .invalidParcels(invalidParcelsToDto(result.importParcelInvalids()))
                .errors(result.errors())
                .build();
    }
}
