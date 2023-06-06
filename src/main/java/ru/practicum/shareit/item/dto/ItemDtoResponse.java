package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDtoResponse {

    private Long id; // уникальный идентификатор вещи;
    private String name; // краткое название;
}
