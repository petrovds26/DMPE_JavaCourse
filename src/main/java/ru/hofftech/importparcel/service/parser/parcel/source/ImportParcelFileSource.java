package ru.hofftech.importparcel.service.parser.parcel.source;

import ru.hofftech.shared.model.enums.FileType;

/**
 * Интерфейс для источников данных из файлов.
 * Расширяет базовый интерфейс ImportParcelSource и добавляет
 * информацию о типе файла.
 *
 * @param <T> тип источника данных (обычно String для пути к файлу)
 */
public interface ImportParcelFileSource<T> extends ImportParcelSource<T> {
    /**
     * Возвращает тип файла, который поддерживает данный источник.
     *
     * @return тип файла из перечисления {@link FileType}
     */
    FileType getFileType();

    /**
     * {@inheritDoc}
     */
    @Override
    default String getDescription() {
        return getFileType().getDescription();
    }
}
