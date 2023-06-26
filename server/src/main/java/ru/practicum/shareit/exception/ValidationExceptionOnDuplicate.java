package ru.practicum.shareit.exception;

public class ValidationExceptionOnDuplicate extends RuntimeException {
    public ValidationExceptionOnDuplicate(String message) {
        super(message);
    }
}
