package ru.hofftech.importparcel.service.parser.parcel.source;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс для источника данных посылок
 */
public interface ImportParcelSource<T> {

    /**
     * Получает блоки строк, где каждый блок - одна посылка
     * @return список блоков строк
     * @throws IOException если возникла ошибка при получении данных
     */
    @NonNull
    List<List<String>> getParcelBlocks(@NonNull T source) throws IOException;

    /**
     * @return описание источника (для логирования)
     */
    @NonNull
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}
