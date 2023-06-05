package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;  // уникальный идентификатор бронирования;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start; // дата и время начала бронирования;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end; // дата и время конца бронирования;
    private BookingStatus status; // статус бронирования.
    private Item item; // вещь, которую пользователь бронирует;
    private Booker booker; // пользователь, который осуществляет бронирование;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Booker {
        private Long id;
//        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }
}