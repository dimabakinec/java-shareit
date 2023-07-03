package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoRequest {

    private Long itemId;
    private LocalDateTime start; // дата и время начала бронирования;
    private LocalDateTime end; // дата и время конца бронирования;
    private final BookingStatus status = WAITING; // статус бронирования.
}