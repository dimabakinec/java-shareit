package ru.practicum.shareit.utils;

public enum Message {
    ADD_MODEL("adding the model {}"),
    UPDATED_MODEL("updating the model with id {} {}"),
    DELETE_MODEL("deleting the model with id {}"),
    REQUEST_ALL("Request all models"),
    REQUEST_BY_ID("model request by id {}"),
    NAME_MAY_NOT_CONTAIN_SPACES("Name may not be empty or contain spaces"),
    DUPLICATE("the model already exists"),
    MODEL_NOT_FOUND("model was not found by the passed ID: "),
    INVALID_USER_ID("invalid user ID "),
    SEARCH("search");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}