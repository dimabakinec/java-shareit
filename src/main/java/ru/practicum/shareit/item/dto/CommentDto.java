package ru.practicum.shareit.item.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {

    @NotBlank
    private String text; // содержимое комментария;

    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private LocalDateTime created = LocalDateTime.now();
}