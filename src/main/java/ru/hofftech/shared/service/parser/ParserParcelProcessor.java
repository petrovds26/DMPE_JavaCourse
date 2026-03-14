package ru.hofftech.shared.service.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.core.TransformParcelResult;
import ru.hofftech.shared.model.dto.ParcelFormDto;
import ru.hofftech.shared.validation.impl.ParcelGridValidator;
import ru.hofftech.shared.validation.impl.ParcelListStringValidator;

import java.util.List;

/**
 * Процессор для трансформации DTO посылки в сущность.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class ParserParcelProcessor {
    @NonNull
    private final ParcelListStringValidator stringValidator;

    @NonNull
    private final ParserParcelNormalizer normalizer;

    @NonNull
    private final ParserParcelBuilder parserParcelBuilder;

    @NonNull
    private final ParcelGridValidator gridValidator;

    /**
     * Трансформирует DTO посылки в сущность с валидацией.
     *
     * @param parcelFormDto DTO с данными посылки
     * @return результат трансформации
     */
    public @NonNull TransformParcelResult transform(@NonNull ParcelFormDto parcelFormDto) {
        TransformParcelResult resultValidateName = validateName(parcelFormDto.name());
        if (resultValidateName.error() != null) {
            return resultValidateName;
        }

        TransformParcelResult resultValidateSymbol = validateSymbol(parcelFormDto.symbol());
        if (resultValidateSymbol.error() != null) {
            return resultValidateSymbol;
        }

        // Шаг 2: Нормализуем форму и получаем сырые блоки строк
        String normalizedForm = parcelFormDto.form().replace("\\n", "\n");
        List<String> rawLines = normalizedForm.lines().toList();

        // Шаг 3: валидация сырых строк
        List<String> stringErrors = stringValidator.validate(rawLines);
        if (!stringErrors.isEmpty()) {
            return TransformParcelResult.builder()
                    .error(String.format("Посылка отклонена на этапе валидации строк. Ошибки: %s", stringErrors))
                    .build();
        }

        // Шаг 4: нормализация
        List<String> normalizedLines = normalizer.normalize(rawLines);

        // Шаг 5: создание посылки
        Parcel parcel =
                parserParcelBuilder.buildFromLines(parcelFormDto.name(), normalizedLines, parcelFormDto.symbol());

        // Шаг 6: валидация готовой посылки
        List<String> gridErrors = gridValidator.validate(parcel);

        if (!gridErrors.isEmpty()) {
            return TransformParcelResult.builder()
                    .error(String.format("Посылка отклонена на этапе валидации grid. Ошибки: %s", gridErrors))
                    .build();
        }

        return TransformParcelResult.builder().parcel(parcel).build();
    }

    /**
     * Валидирует название посылки.
     *
     * @param name название для проверки
     * @return результат валидации
     */
    public @NonNull TransformParcelResult validateName(@Nullable String name) {
        if (name == null || name.isBlank()) {
            return TransformParcelResult.builder()
                    .error("В посылке не указано название.")
                    .build();
        }

        return TransformParcelResult.builder().build();
    }

    /**
     * Валидирует символ посылки.
     *
     * @param symbol символ для проверки
     * @return результат валидации
     */
    public @NonNull TransformParcelResult validateSymbol(@Nullable String symbol) {
        if (symbol == null || symbol.length() != 1) {
            return TransformParcelResult.builder()
                    .error("Длина символа не равна 1.")
                    .build();
        }

        if (Character.isWhitespace(symbol.charAt(0))) {
            return TransformParcelResult.builder()
                    .error("Символ не может быть пробелом.")
                    .build();
        }

        return TransformParcelResult.builder().build();
    }
}
