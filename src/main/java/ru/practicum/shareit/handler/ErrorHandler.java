package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.ValidationExceptionOnDuplicate;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException validationException) {
        log.error("Код ошибки: {}, {}", HttpStatus.BAD_REQUEST, validationException.getMessage());
        return Map.of("error", validationException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException notFoundException) {
        log.error("Код ошибки: {}, {}", HttpStatus.NOT_FOUND, notFoundException.getMessage());
        return Map.of("error", notFoundException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleValidationExceptionOnDuplicate(final ValidationExceptionOnDuplicate exception) {
        log.error("Код ошибки: {}, {}", HttpStatus.CONFLICT, exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}