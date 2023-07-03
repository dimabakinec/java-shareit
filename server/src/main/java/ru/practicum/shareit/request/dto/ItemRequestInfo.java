package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestInfo {

    private Long id; // уникальный идентификатор запроса;
    private String description; // текст запроса, содержащий описание требуемой вещи;
    private LocalDateTime created; // дата и время создания запроса.
    private Set<ItemDto> items = new HashSet<>();
}