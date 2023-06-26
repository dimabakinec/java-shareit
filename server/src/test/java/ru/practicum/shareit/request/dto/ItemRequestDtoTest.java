package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws IOException {
        ItemRequestDto requestDto = new ItemRequestDto(
          1L,
          "Description",
          LocalDateTime.of(2023, 5, 22, 1, 34, 1)
        );

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo("2023-05-22T01:34:01");
    }

    @Test
    void testItemRequestDtoWithNull() throws IOException {
        ItemRequestDto requestDto = new ItemRequestDto(
                null,
                "Description",
                LocalDateTime.of(2023, 5, 22, 1, 34, 1)
        );

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).doesNotHaveJsonPathValue("$.id");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo("2023-05-22T01:34:01");
    }
}