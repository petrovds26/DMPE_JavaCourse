package ru.hofftech.shared.service.parser.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.core.Machine;
import ru.hofftech.shared.model.core.ParserMachineProcessorResult;
import ru.hofftech.shared.service.parser.ParserMachineProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Парсер для создания машин из строк формата "[ширина]x[высота]".
 * Пример: "3x3\n6x2\n5x5"
 */
@Slf4j
@RequiredArgsConstructor
public class ParserMachineFromFormString implements ParserMachineProcessor<String> {

    private static final Pattern MACHINE_PATTERN = Pattern.compile("^(\\d+)x(\\d+)$");
    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull ParserMachineProcessorResult transform(@NonNull String inputFormString) {
        List<Machine> machines = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Шаг 1: Нормализуем строку и разбиваем на строки
        String normalizedInput = inputFormString.replace("\\n", "\n");
        List<String> rawLines = normalizedInput.lines().toList();

        // Шаг 2: Обрабатываем каждую строку
        for (int i = 0; i < rawLines.size(); i++) {
            String line = rawLines.get(i).trim();

            if (line.isEmpty()) {
                continue; // Пропускаем пустые строки
            }

            log.debug("Обработка строки #{}: '{}'", i + 1, line);

            processMachineLine(line, i + 1, machines, errors);
        }

        log.info("Создано машин: {}, ошибок: {}", machines.size(), errors.size());

        return ParserMachineProcessorResult.builder()
                .machines(machines)
                .errors(errors)
                .build();
    }

    /**
     * Обрабатывает одну строку с описанием машины.
     *
     * @param line       строка для обработки (не может быть null)
     * @param lineNumber номер строки (для сообщений об ошибках)
     * @param machines   список успешно созданных машин (не может быть null)
     * @param errors     список ошибок (не может быть null)
     */
    private void processMachineLine(
            @NonNull String line, int lineNumber, @NonNull List<Machine> machines, @NonNull List<String> errors) {

        var matcher = MACHINE_PATTERN.matcher(line);

        if (!matcher.matches()) {
            errors.add(String.format(
                    "Строка %d: '%s' - неверный формат. Ожидается формат '[ширина]x[высота]' (например, 3x3)",
                    lineNumber, line));
            return;
        }

        try {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));

            if (width <= 0 || height <= 0) {
                errors.add(String.format(
                        "Строка %d: '%s' - ширина и высота должны быть положительными числами", lineNumber, line));
                return;
            }

            Machine machine = new Machine(width, height);
            machines.add(machine);

            log.debug("Создана машина {}x{} из строки {}", width, height, lineNumber);

        } catch (NumberFormatException e) {
            errors.add(String.format(
                    "Строка %d: '%s' - ошибка преобразования чисел: %s", lineNumber, line, e.getMessage()));
        }
    }
}
