package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentDtoResponse {

    private long id; // уникальный идентификатор комментария;
    private String text; // содержимое комментария;
    private String authorName; // автор комментария;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSS")
    private LocalDateTime created; // дата создания комментария.
}