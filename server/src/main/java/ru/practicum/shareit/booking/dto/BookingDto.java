package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;  // уникальный идентификатор бронирования;
    private LocalDateTime start; // дата и время начала бронирования;
    private LocalDateTime end; // дата и время конца бронирования;
    private BookingStatus status; // статус бронирования.
    private ItemDtoResponse item; // вещь, которую пользователь бронирует;
    private UserDtoResponse booker; // пользователь, который осуществляет бронирование;
}
