package ru.hofftech.importparcel.util;

import lombok.experimental.UtilityClass;
import ru.hofftech.importparcel.model.core.ImportParcelInvalid;
import ru.hofftech.importparcel.model.core.ImportParcelResult;
import ru.hofftech.importparcel.model.dto.ImportParcelInvalidDto;
import ru.hofftech.importparcel.model.dto.ImportParcelOutputResultDto;
import ru.hofftech.shared.util.MapperUtil;

import java.util.List;

@UtilityClass
public class ImportParcelMapperUtil {

    /**
     * Преобразует ImportParcelInvalid в ImportParcelInvalidDto
     */
    public ImportParcelInvalidDto invalidParcelToDto(ImportParcelInvalid importParcelInvalid) {
        if (importParcelInvalid == null) {
            return null;
        }

        return ImportParcelInvalidDto.builder()
                .parcel(MapperUtil.parcelToDto(importParcelInvalid.parcel()))
                .cause(importParcelInvalid.cause())
                .build();
    }

    /**
     * Преобразует список ImportParcelInvalid в список DTO
     */
    public List<ImportParcelInvalidDto> invalidParcelsToDto(List<ImportParcelInvalid> importParcelInvalids) {
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
    public ImportParcelOutputResultDto loadingResultToOutputDto(ImportParcelResult result) {
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
