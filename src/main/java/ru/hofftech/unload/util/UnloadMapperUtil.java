package ru.hofftech.unload.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.util.MapperUtil;
import ru.hofftech.unload.model.core.UnloadResult;
import ru.hofftech.unload.model.dto.UnloadResultDto;

@UtilityClass
public class UnloadMapperUtil {

    /**
     * Преобразует UnloadResult в UnloadResultDto
     */
    @Nullable
    public UnloadResultDto loadingResultToOutputDto(UnloadResult result) {
        if (result == null) {
            return null;
        }

        return UnloadResultDto.builder()
                .parcels(result.parcels().stream().map(MapperUtil::parcelToDto).toList())
                .build();
    }
}
