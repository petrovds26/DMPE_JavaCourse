package ru.hofftech.telegram.validation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.hofftech.telegram.exception.ValidateException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Сервис для валидации входных параметров, получаемых от пользователя в Telegram.
 */
@Service
@NullMarked
@RequiredArgsConstructor
public class InputParametersValidator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Валидирует название посылки.
     *
     * @param name название посылки
     * @throws ValidateException если название пустое или состоит только из пробелов
     */
    public void validateName(@Nullable String name) {
        if (stringIsNullOrBlank(name)) {
            throw new ValidateException("Название не может быть пустым.");
        }
    }

    /**
     * Валидирует символ посылки.
     *
     * @param parcelSymbol символ посылки
     * @throws ValidateException если символ не является одним символом, является пробелом или управляющим символом
     */
    public void validateParcelSymbol(@Nullable String parcelSymbol) {
        if (stringIsNullOrBlank(parcelSymbol) || parcelSymbol.length() != 1) {
            throw new ValidateException("Символ должен быть одним символом.");
        }

        char symbol = parcelSymbol.charAt(0);

        if (Character.isWhitespace(symbol)) {
            throw new ValidateException("Символ не может быть пробелом.");
        }

        if (Character.isISOControl(symbol)) {
            throw new ValidateException("Символ не может быть управляющим символом.");
        }
    }

    /**
     * Валидирует форму посылки.
     *
     * @param parcelForm форма посылки
     * @throws ValidateException если форма пустая или состоит только из пробелов
     */
    public void validateParcelForm(@Nullable String parcelForm) {
        if (stringIsNullOrBlank(parcelForm)) {
            throw new ValidateException("Форма не может быть пустой.");
        }
    }

    /**
     * Валидирует идентификатор пользователя.
     *
     * @param userId идентификатор пользователя
     * @throws ValidateException если идентификатор пустой или состоит только из пробелов
     */
    public void validateUserId(@Nullable String userId) {
        if (stringIsNullOrBlank(userId)) {
            throw new ValidateException("Id пользователя не может быть пустым.");
        }
    }

    /**
     * Валидирует дату в формате дд.ММ.гггг.
     * <p>
     * Если дата не указана (null или пустая строка), валидация считается успешной.
     *
     * @param dateStr строка с датой
     * @throws ValidateException если дата указана, но имеет неверный формат
     */
    public void validDate(@Nullable String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return;
        }

        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            throw new ValidateException(
                    String.format("Неверный формат даты: %s. Ожидается формат dd.MM.yyyy", dateStr));
        }
    }

    /**
     * Проверяет, является ли строка null, пустой или состоящей только из пробелов.
     *
     * @param str проверяемая строка
     * @return true если строка null, пустая или состоит только из пробелов
     */
    private boolean stringIsNullOrBlank(@Nullable String str) {
        return str == null || str.trim().isEmpty();
    }
}
