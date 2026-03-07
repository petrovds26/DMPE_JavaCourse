package ru.hofftech.importmachine.service.parser.source;

import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.dto.MachineDto;
import ru.hofftech.shared.model.enums.FileType;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс для источника данных машин
 */
public interface ImportMachineFileSource<T> {

    /**
     * Получает список машин из источника
     * @return список машин
     * @throws IOException если возникла ошибка при получении данных
     */
    @NonNull
    List<MachineDto> getMachines(@NonNull T source) throws IOException;

    /**
     * @return тип источника из enum
     */
    @NonNull
    FileType getFileType();

    /**
     * @return описание источника (для логирования)
     */
    @NonNull
    default String getDescription() {
        return getFileType().getDescription();
    }
}
