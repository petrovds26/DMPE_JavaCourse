package ru.hofftech.importmachine.util;

import lombok.experimental.UtilityClass;
import ru.hofftech.importmachine.model.core.ImportMachineResult;
import ru.hofftech.importmachine.model.dto.ImportMachineOutputResultDto;
import ru.hofftech.shared.util.MapperUtil;

@UtilityClass
public class ImportMachineMapperUtil {

    /**
     * Преобразует ImportMachineResult в ImportMachineOutputResultDto
     */
    public ImportMachineOutputResultDto loadingResultToOutputDto(ImportMachineResult result) {
        if (result == null) {
            return null;
        }

        return ImportMachineOutputResultDto.builder()
                .parcels(result.parcels().stream().map(MapperUtil::parcelToDto).toList())
                .build();
    }
}
