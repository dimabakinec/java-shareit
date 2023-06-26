package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoForItem {

    private Long id;  // уникальный идентификатор бронирования;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId; // пользователь, который осуществляет бронирование;
}