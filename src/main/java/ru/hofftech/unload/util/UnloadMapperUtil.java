package ru.hofftech.unload.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.util.MapperUtil;
import ru.hofftech.unload.model.core.UnloadResult;
import ru.hofftech.unload.model.dto.UnloadResultDto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NullMarked
@UtilityClass
public class UnloadMapperUtil {

    /**
     * Преобразует UnloadResult в UnloadResultDto
     */
    @Nullable
    public UnloadResultDto loadingResultToOutputDto(@Nullable UnloadResult result) {
        if (result == null) {
            return null;
        }

        List<ParcelDto> parcels = result.parcels().stream()
                .map(MapperUtil::parcelToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        return UnloadResultDto.builder().parcels(parcels).build();
    }
}
