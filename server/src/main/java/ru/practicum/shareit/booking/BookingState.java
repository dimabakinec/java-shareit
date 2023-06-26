package ru.practicum.shareit.booking;

public enum BookingState {
    ALL("все"),
    CURRENT("текущие"),
    PAST("завершённые"),
    FUTURE("будущие"),
    WAITING("ожидающие подтверждения"),
    REJECTED("отклонённые"),
    UNSUPPORTED_STATUS("неподдерживаемый статус");

    private final String message;

    BookingState(String message) {
        this.message = message;
    }

    public static BookingState valueOfIgnoreCase(String name) {
        return BookingState.valueOf(name.toUpperCase());
    }
}