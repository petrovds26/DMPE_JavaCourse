package ru.hofftech.console.service.parcer.transform.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import ru.hofftech.console.service.parcer.transform.TransformToStringListStrategy;

import java.util.List;

/**
 * Стратегия преобразования текстовой строки в список строк.
 * <p>
 * Заменяет экранированные символы перевода строки (\n) на реальные
 * и разбивает строку на список.
 */
@NullMarked
@RequiredArgsConstructor
@Component
@Slf4j
public class TransformTextToStringList implements TransformToStringListStrategy {

    /**
     * {@inheritDoc}
     * <p>
     * Заменяет "\n" на реальные символы перевода строки и разбивает на список.
     *
     * @param inputString исходная строка
     * @return список строк
     */
    @Override
    public List<String> transform(String inputString) {

        String normalizedTxtName = inputString.replace("\\n", "\n");
        return normalizedTxtName.lines().toList();
    }
}
