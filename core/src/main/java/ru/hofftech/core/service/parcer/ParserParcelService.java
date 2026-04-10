package ru.hofftech.core.service.parcer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.hofftech.core.exception.ParcelException;
import ru.hofftech.core.model.core.Parcel;
import ru.hofftech.core.model.core.ParserParcelProcessorResult;
import ru.hofftech.core.validation.impl.ParcelGridValidator;
import ru.hofftech.core.validation.impl.ParcelListStringValidator;
import ru.hofftech.shared.model.dto.ParcelFormRequestDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Процессор для трансформации DTO посылки в сущность.
 * <p>
 * Обрабатывает список DTO, создавая посылки или собирая ошибки.
 */
@Slf4j
@RequiredArgsConstructor
@NullMarked
@Service
public class ParserParcelService {
    private final ParcelListStringValidator stringValidator;

    private final ParserParcelNormalizer normalizer;

    private final ParserParcelBuilder parserParcelBuilder;

    private final ParcelGridValidator gridValidator;

    /**
     * Трансформирует список DTO посылок в сущности с валидацией каждой.
     * <p>
     * Каждый элемент обрабатывается независимо, ошибки не прерывают процесс.
     *
     * @param parcelFormRequestDtoList список DTO с данными посылок
     * @return результат трансформации со всеми успешными и ошибочными элементами
     */
    public ParserParcelProcessorResult transform(List<ParcelFormRequestDto> parcelFormRequestDtoList) {
        List<Parcel> successfulParcels = new ArrayList<>();
        List<String> allErrors = new ArrayList<>();

        for (int i = 0; i < parcelFormRequestDtoList.size(); i++) {
            ParcelFormRequestDto dto = parcelFormRequestDtoList.get(i);
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
     * @param name название для проверки
     * @return результат валидации
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
     * @param symbol символ для проверки
     * @return результат валидации
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
     * Создаёт посылку из названия, нормализованных строк и символа.
     *
     * @param name            название посылки
     * @param normalizedLines нормализованные строки формы
     * @param symbol          символ посылки
     * @return созданная посылка
     * @throws ParcelException если произошла ошибка при создании
     */
    public Parcel buildParcel(String name, List<String> normalizedLines, String symbol) {
        try {
            return parserParcelBuilder.buildFromLines(name, normalizedLines, symbol);
        } catch (Exception e) {
            throw new ParcelException("Ошибка при создании посылки: " + e.getMessage(), e);
        }
    }

    /**
     * Трансформирует один DTO посылки в сущность с валидацией.
     *
     * @param parcelFormRequestDto DTO с данными посылки
     * @return результат трансформации для одного элемента
     */
    private ParserParcelProcessorResult transformSingle(ParcelFormRequestDto parcelFormRequestDto) {
        // Шаг 1: Валидация названия
        ParserParcelProcessorResult resultValidateName = validateName(parcelFormRequestDto.name());
        if (resultValidateName.hasErrors()) {
            return resultValidateName;
        }

        // Шаг 2: Валидация символа
        ParserParcelProcessorResult resultValidateSymbol = validateSymbol(parcelFormRequestDto.symbol());
        if (resultValidateSymbol.hasErrors()) {
            return resultValidateSymbol;
        }

        // Шаг 3: Нормализуем форму и получаем сырые блоки строк
        String normalizedForm = parcelFormRequestDto.form().replace("\\n", "\n");
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
            parcel = buildParcel(parcelFormRequestDto.name(), normalizedLines, parcelFormRequestDto.symbol());
        } catch (ParcelException e) {
            log.error(e.getMessage(), e);
            return createDefaultErrorResult(e.getMessage());
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
     * @return пустой результат
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
     * @return результат с ошибкой
     */
    private ParserParcelProcessorResult createDefaultErrorResult(String error) {
        return ParserParcelProcessorResult.builder()
                .parcels(List.of())
                .errors(List.of(error))
                .build();
    }
}
