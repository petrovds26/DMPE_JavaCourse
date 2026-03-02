package ru.hofftech.importparcel.service.parser.parcel.source;

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
    List<List<String>> getParcelBlocks(T source) throws IOException;

    /**
     * @return описание источника (для логирования)
     */
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}
