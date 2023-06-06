package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class ItemInfo {

    private Long id; // уникальный идентификатор вещи;
    private String name; // краткое название;
    private String description; // развёрнутое описание;
    private Boolean available; // статус о том, доступна или нет вещь для аренды;
    private BookingDtoForItem lastBooking; // последнее бронирование;
    private BookingDtoForItem nextBooking; // ближайшее следующее бронирование;
    private Set<CommentDtoResponse> comments = new HashSet<>();
}