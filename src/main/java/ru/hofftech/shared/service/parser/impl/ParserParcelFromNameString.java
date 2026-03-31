package ru.hofftech.shared.service.parser.impl;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.ParserParcelProcessorResult;
import ru.hofftech.shared.repository.ParcelRepository;
import ru.hofftech.shared.service.parser.ParserParcelProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Парсер для получения посылок из строки с названиями.
 * Строка может содержать несколько названий, разделённых переносом строки.
 */
@NullMarked
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class ParserParcelFromNameString implements ParserParcelProcessor<String> {

    public final ParcelRepository parcelRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ParserParcelProcessorResult transform(String inputNameString) {

        List<Parcel> parcels = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        // Шаг 1: Нормализуем строку, расставляя символы переноса строк и делим строку на список строк
        String normalizedTxtName = inputNameString.replace("\\n", "\n");
        List<String> rawLines = normalizedTxtName.lines().toList();
        // Для каждой строки распознаем название посылки и попытаемся найти в репозитории
        for (String rawLine : rawLines) {
            parcelRepository
                    .find(rawLine)
                    .ifPresentOrElse(parcels::add, () -> errors.add("Не найдена посылка с именем " + rawLine));
        }

        return ParserParcelProcessorResult.builder()
                .parcels(parcels)
                .errors(errors)
                .build();
    }
}
