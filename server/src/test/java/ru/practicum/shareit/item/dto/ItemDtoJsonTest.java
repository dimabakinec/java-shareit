package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws IOException {
        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Description",
                true,
                1L
        );

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testItemDtoWithNull() throws IOException {
        ItemDto itemDto = new ItemDto(
                null,
                "Name",
                "Description",
                true,
                null
        );

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).doesNotHaveJsonPathValue("$.id");
        assertThat(result).doesNotHaveJsonPathValue("$.requestId");
    }
}