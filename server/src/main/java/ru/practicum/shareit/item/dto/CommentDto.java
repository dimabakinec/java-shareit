package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private String text; // содержимое комментария;
    private LocalDateTime created = LocalDateTime.now();
}