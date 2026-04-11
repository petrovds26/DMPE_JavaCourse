package ru.hofftech.core.exception.advice;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.hofftech.core.exception.JsonUtilException;
import ru.hofftech.core.exception.LoadException;
import ru.hofftech.core.exception.ParcelException;
import ru.hofftech.core.util.ResponseWrapperUtil;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.enums.ResponseCode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * <p>
 * Перехватывает исключения, возникающие при обработке запросов,
 * и преобразует их в стандартный формат ответа Response.
 */
@Slf4j
@NullMarked
@RestControllerAdvice
public class ResponseControllerAdvice {
    private static final String ERROR = "Произошла ошибка!";

    /**
     * Обрабатывает исключения, связанные с некорректными запросами.
     *
     * @param exception исключение
     * @return ответ с кодом BAD_REQUEST
     */
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MissingServletRequestParameterException.class,
        IllegalArgumentException.class,
        MissingRequestHeaderException.class,
        ParcelException.class,
        LoadException.class,
        JsonUtilException.class
    })
    public Response<Object> handle(RuntimeException exception) {
        log.error(ERROR, exception);
        Response<Object> response = new Response<>(ResponseCode.BAD_REQUEST);
        response.getResult().setMessage(exception.getMessage());
        return response;
    }

    /**
     * Обрабатывает исключения валидации ограничений.
     *
     * @param exception исключение ConstraintViolationException
     * @return ответ с кодом BAD_REQUEST и сообщением об ошибках валидации
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Object> handle(ConstraintViolationException exception) {
        log.error(ERROR, exception);
        String message = exception.getConstraintViolations().stream()
                .map(constraint -> constraint.getPropertyPath().toString() + " " + constraint.getMessage())
                .collect(Collectors.joining(" and "));
        Response<Object> response = new Response<>(ResponseCode.BAD_REQUEST);
        response.getResult().setMessage(message);
        return response;
    }

    /**
     * Обрабатывает исключения валидации аргументов метода.
     *
     * @param exception исключение MethodArgumentNotValidException
     * @return ответ с кодом BAD_REQUEST и сообщением об ошибках полей
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Object> handle(MethodArgumentNotValidException exception) {
        log.error(ERROR, exception);
        Response<Object> response = new Response<>(ResponseCode.BAD_REQUEST);
        response.getResult().setMessage(defineErrorMessage(exception));
        return response;
    }

    /**
     * Обрабатывает все неперехваченные исключения.
     *
     * @param exception исключение
     * @return ответ с кодом INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handle(Exception exception) {
        log.error(ERROR, exception);

        return ResponseWrapperUtil.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * Формирует сообщение об ошибках валидации из MethodArgumentNotValidException.
     *
     * @param exception исключение
     * @return строка с перечислением ошибок полей
     */
    private String defineErrorMessage(MethodArgumentNotValidException exception) {
        StringBuilder message = new StringBuilder();
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        fieldErrors.forEach(error -> message.append(error.getField())
                .append(" : ")
                .append(error.getDefaultMessage())
                .append("; "));

        return !message.isEmpty() ? message.substring(0, message.length() - 2) : "Ошибка валидации.";
    }
}
