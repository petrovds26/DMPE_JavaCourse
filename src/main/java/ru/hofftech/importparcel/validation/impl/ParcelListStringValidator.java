package ru.hofftech.importparcel.validation.impl;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Валидатор для сырых строк посылки до нормализации
 * Проверяет:
 * - Наличие символов в посылке
 * - Консистентность символов (все символы "не пробел" должны быть одинаковы)
 */
@Slf4j
public class ParcelListStringValidator implements Validator<List<String>> {

    @Override
    @NonNull
    public List<String> validate(@Nullable List<String> lines) {
        List<String> errors = new ArrayList<>();

        if (lines == null || lines.isEmpty()) {
            errors.add("Посылка не содержит строк");
            return errors;
        }

        validateNotEmptyLines(errors, lines);
        validateConsistentSymbol(errors, lines);

        return errors;
    }

    /**
     * Проверяет, что строки не пустые и содержат символы
     */
    private void validateNotEmptyLines(@NonNull List<String> errors, @NonNull List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.trim().isEmpty()) {
                errors.add(String.format("Строка %d пустая", i + 1));
            }
        }
    }

    /**
     * Проверяет, что все символы "не пробел" в посылке одинаковы
     */
    private void validateConsistentSymbol(@NonNull List<String> errors, @NonNull List<String> lines) {
        // Находим первый символ в посылке
        Character firstSymbol = findFirstSymbol(lines);

        if (firstSymbol == null) {
            errors.add("Посылка не содержит символов");
            return;
        }

        // Проверяем каждую строку
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            Set<Character> invalidChars = line.chars()
                    .mapToObj(c -> (char) c)
                    .filter(c -> !c.equals(firstSymbol) && !Character.isWhitespace(c))
                    .collect(Collectors.toSet());

            if (!invalidChars.isEmpty()) {
                errors.add(String.format(
                        "Строка %d содержит недопустимые символы %s (ожидался '%c')",
                        i + 1, invalidChars, firstSymbol));
            }
        }
    }

    /**
     * Находит первый символ "не пробел" в посылке.
     *
     * @param lines список строк посылки
     * @return первый найденный символ или null, если символов нет
     */
    @Nullable
    private Character findFirstSymbol(@NonNull List<String> lines) {
        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (!Character.isWhitespace(c)) {
                    return c;
                }
            }
        }
        return null;
    }
}
