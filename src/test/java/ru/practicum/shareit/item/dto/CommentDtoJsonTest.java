package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDtoResponse> json;

    @Test
    void testCommentDto() throws IOException {
        CommentDtoResponse commentDto = new CommentDtoResponse(
          1L,
          "Comment",
          "user",
          LocalDateTime.of(2023, 5, 22, 1, 34, 1)
        );

        JsonContent<CommentDtoResponse> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo("2023-05-22T01:34:01.0000");
    }
}