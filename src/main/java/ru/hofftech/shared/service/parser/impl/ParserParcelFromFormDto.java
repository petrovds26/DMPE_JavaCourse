package ru.hofftech.shared.service.parser.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.ParserParcelProcessorResult;
import ru.hofftech.shared.model.dto.ParcelFormDto;
import ru.hofftech.shared.service.parser.ParserParcelBuilder;
import ru.hofftech.shared.service.parser.ParserParcelNormalizer;
import ru.hofftech.shared.service.parser.ParserParcelProcessor;
import ru.hofftech.shared.validation.impl.ParcelGridValidator;
import ru.hofftech.shared.validation.impl.ParcelListStringValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Процессор для трансформации DTO посылки в сущность.
 * Обрабатывает список DTO, создавая посылки или собирая ошибки.
 */
@Slf4j
@RequiredArgsConstructor
@NullMarked
@SuppressWarnings("ClassCanBeRecord")
public class ParserParcelFromFormDto implements ParserParcelProcessor<List<ParcelFormDto>> {
    private final ParcelListStringValidator stringValidator;

    private final ParserParcelNormalizer normalizer;

    private final ParserParcelBuilder parserParcelBuilder;

    private final ParcelGridValidator gridValidator;

    /**
     * Трансформирует список DTO посылок в сущности с валидацией каждой.
     * Каждый элемент обрабатывается независимо, ошибки не прерывают процесс.
     *
     * @param parcelFormDtoList список DTO с данными посылок (не может быть null)
     * @return результат трансформации со всеми успешными и ошибочными элементами (не может быть null)
     */
    public ParserParcelProcessorResult transform(List<ParcelFormDto> parcelFormDtoList) {
        List<Parcel> successfulParcels = new ArrayList<>();
        List<String> allErrors = new ArrayList<>();

        for (int i = 0; i < parcelFormDtoList.size(); i++) {
            ParcelFormDto dto = parcelFormDtoList.get(i);
            log.debug("Обработка элемента #{}: {}", i + 1, dto.name());

            ParserParcelProcessorResult elementResult = transformSingle(dto);

            if (elementResult.hasErrors()) {
                // Добавляем ошибки с указанием индекса элемента для ясности
                String elementErrors = elementResult.getErrorsAsString();
                allErrors.add(String.format("Элемент #%d (%s): %s", i + 1, dto.name(), elementErrors));
            } else {
                successfulParcels.addAll(elementResult.parcels());
            }
        }

        return ParserParcelProcessorResult.builder()
                .parcels(successfulParcels)
                .errors(allErrors)
                .build();
    }

    /**
     * Валидирует название посылки.
     *
     * @param name название для проверки (может быть null)
     * @return результат валидации (не может быть null)
     */
    public ParserParcelProcessorResult validateName(@Nullable String name) {
        if (name == null || name.isBlank()) {
            return createDefaultErrorResult("Название посылки не указано");
        }
        return createDefaultResult();
    }

    /**
     * Валидирует символ посылки.
     *
     * @param symbol символ для проверки (может быть null)
     * @return результат валидации (не может быть null)
     */
    public ParserParcelProcessorResult validateSymbol(@Nullable String symbol) {
        if (symbol == null || symbol.length() != 1) {
            return createDefaultErrorResult("Символ должен быть одним символом");
        }

        if (Character.isWhitespace(symbol.charAt(0))) {
            return createDefaultErrorResult("Символ не может быть пробелом");
        }

        return createDefaultResult();
    }

    /**
     * Трансформирует один DTO посылки в сущность с валидацией.
     *
     * @param parcelFormDto DTO с данными посылки (не может быть null)
     * @return результат трансформации для одного элемента (не может быть null)
     */
    private ParserParcelProcessorResult transformSingle(ParcelFormDto parcelFormDto) {
        // Шаг 1: Валидация названия
        ParserParcelProcessorResult resultValidateName = validateName(parcelFormDto.name());
        if (resultValidateName.hasErrors()) {
            return resultValidateName;
        }

        // Шаг 2: Валидация символа
        ParserParcelProcessorResult resultValidateSymbol = validateSymbol(parcelFormDto.symbol());
        if (resultValidateSymbol.hasErrors()) {
            return resultValidateSymbol;
        }

        // Шаг 3: Нормализуем форму и получаем сырые блоки строк
        String normalizedForm = parcelFormDto.form().replace("\\n", "\n");
        List<String> rawLines = normalizedForm.lines().toList();

        // Шаг 4: Валидация сырых строк
        List<String> stringErrors = stringValidator.validate(rawLines);
        if (!stringErrors.isEmpty()) {
            return createDefaultErrorResult(String.format("Ошибки валидации строк: %s", stringErrors));
        }

        // Шаг 5: Нормализация
        List<String> normalizedLines = normalizer.normalize(rawLines);

        // Шаг 6: Создание посылки
        Parcel parcel;
        try {
            parcel = parserParcelBuilder.buildFromLines(parcelFormDto.name(), normalizedLines, parcelFormDto.symbol());
        } catch (Exception e) {
            log.error("Ошибка при создании посылки", e);
            return createDefaultErrorResult("Ошибка при создании посылки: " + e.getMessage());
        }

        // Шаг 7: Валидация готовой посылки
        List<String> gridErrors = gridValidator.validate(parcel);
        if (!gridErrors.isEmpty()) {
            return createDefaultErrorResult(String.format("Ошибки валидации формы: %s", gridErrors));
        }

        return ParserParcelProcessorResult.builder()
                .parcels(List.of(parcel))
                .errors(List.of())
                .build();
    }

    /**
     * Создаёт пустой результат без ошибок.
     *
     * @return пустой результат (не может быть null)
     */
    private ParserParcelProcessorResult createDefaultResult() {
        return ParserParcelProcessorResult.builder()
                .parcels(List.of())
                .errors(List.of())
                .build();
    }

    /**
     * Создаёт результат с одной ошибкой.
     *
     * @param error текст ошибки
     * @return результат с ошибкой (не может быть null)
     */
    private ParserParcelProcessorResult createDefaultErrorResult(String error) {
        return ParserParcelProcessorResult.builder()
                .parcels(List.of())
                .errors(List.of(error))
                .build();
    }
}
