package ru.practicum.shareit.utils;

public enum Message {
    ADD_MODEL("adding the model {}"),
    UPDATED_MODEL("updating the model with id {} {}"),
    DELETE_MODEL("deleting the model with id {}"),
    REQUEST_ALL("Request all models"),
    REQUEST_BY_ID("model request by id {}"),
    NAME_MAY_NOT_CONTAIN_SPACES("Name may not be empty or contain spaces"),
    MODEL_NOT_FOUND("model was not found by the passed ID: "),
    NOT_AVAILABLE("Item is not available for booking"),
    INVALID_DATE("Incorrect start or end date. " +
            "The end date cannot be equal to the start date and cannot be before the start date."),
    INVALID_USER_REQUEST_APPROVED("Approved or rejection of a booking request" +
            " can only be performed by the owner of the item."),
    UNKNOWN_STATE("Unknown state: UNSUPPORTED_STATUS"),
    BEEN_APPROVED("the booking has already been approved"),
    IS_OWNER_ITEM("the user is the owner of the item. the owner cannot book his item"),
    NOT_ADD_COMMENT("The booker can add a comment after the end of the lease term"),
    SEARCH("search");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}