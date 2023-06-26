package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING("новое бронирование, ожидает одобрения"),
    APPROVED("бронирование подтверждено владельцем"),
    REJECTED("бронирование отклонено владельцем"),
    CANCELED("бронирование отменено создателем");

    private final String message;

    BookingStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}