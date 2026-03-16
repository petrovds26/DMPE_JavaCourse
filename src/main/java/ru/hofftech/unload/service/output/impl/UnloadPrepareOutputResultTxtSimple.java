package ru.hofftech.unload.service.output.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.ProcessorCommandResult;
import ru.hofftech.unload.model.core.UnloadResult;
import ru.hofftech.unload.service.output.UnloadPrepareOutputResult;

import java.util.List;

/**
 * Вывод результата разгрузки машин в текстовый файл
 * Формат: посылки разделены пустыми строками
 */
@Slf4j
@RequiredArgsConstructor
public class UnloadPrepareOutputResultTxtSimple implements UnloadPrepareOutputResult {

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessorCommandResult output(@NonNull UnloadResult result) {
        String txt = formatParcelsToText(result.parcels());

        return ProcessorCommandResult.builder().success(true).message(txt).build();
    }

    /**
     * Форматирует список посылок в текстовый формат
     * Посылки разделяются пустыми строками
     */
    @NonNull
    private String formatParcelsToText(@NonNull List<Parcel> parcels) {
        if (parcels.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parcels.size(); i++) {
            Parcel parcel = parcels.get(i);

            // Добавляем строки посылки
            sb.append(parcel.getForm()).append("\n");

            // Добавляем пустую строку между посылками (кроме последней)
            if (i < parcels.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
