package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Long id; // уникальный идентификатор запроса;
    private String description; // текст запроса, содержащий описание требуемой вещи;
    private LocalDateTime created;
}