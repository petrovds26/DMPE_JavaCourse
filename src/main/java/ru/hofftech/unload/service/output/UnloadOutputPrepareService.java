package ru.hofftech.unload.service.output;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.unload.model.enums.UnloadOutputType;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для управления подготовкой вывода результатов в зависимости от типа вывода.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class UnloadOutputPrepareService {

    @NonNull
    private final Map<UnloadOutputType, UnloadPrepareOutputResult> outputPreparers;

    /**
     * Получает подготовитель вывода по типу.
     *
     * @param type тип вывода (не может быть null)
     * @return подготовитель вывода или null, если не найден
     */
    @Nullable
    public UnloadPrepareOutputResult getPreparer(@NonNull UnloadOutputType type) {
        if (supports(type)) {
            return outputPreparers.get(type);
        }
        return null;
    }

    /**
     * Проверяет, поддерживается ли указанный тип вывода.
     *
     * @param type тип вывода (не может быть null)
     * @return true если поддерживается
     */
    private boolean supports(@NonNull UnloadOutputType type) {
        return outputPreparers.containsKey(type);
    }

    /**
     * Возвращает описание всех поддерживаемых типов вывода.
     *
     * @return строка с описанием (не может быть null)
     */
    @NonNull
    public String getAvailableTypesDescription() {
        return outputPreparers.keySet().stream()
                .map(type -> String.format("%d - %s", type.getId(), type.getDescription()))
                .collect(Collectors.joining("; "));
    }
}
