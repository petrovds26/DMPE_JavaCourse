package ru.hofftech.load.service.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.load.model.enums.LoadInputParcelType;
import ru.hofftech.shared.service.parser.ParserParcelProcessor;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для управления парсерами посылок в зависимости от типа ввода.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class LoadParcelParserService {

    @NonNull
    private final Map<LoadInputParcelType, ParserParcelProcessor<String>> parsers;

    /**
     * Получает парсер по типу ввода.
     *
     * @param type тип ввода (не может быть null)
     * @return парсер или null, если не найден
     */
    @Nullable
    public ParserParcelProcessor<String> getParser(@NonNull LoadInputParcelType type) {
        if (supports(type)) {
            return parsers.get(type);
        }
        return null;
    }

    /**
     * Проверяет, поддерживается ли указанный тип ввода.
     *
     * @param type тип ввода (не может быть null)
     * @return true если поддерживается
     */
    private boolean supports(@NonNull LoadInputParcelType type) {
        return parsers.containsKey(type);
    }

    /**
     * Возвращает описание всех поддерживаемых типов ввода.
     *
     * @return строка с описанием (не может быть null)
     */
    @NonNull
    public String getAvailableTypesDescription() {
        return parsers.keySet().stream()
                .map(type -> String.format("%d - %s", type.getId(), type.getDescription()))
                .collect(Collectors.joining("; "));
    }
}
