package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    @Test
    void testBookingDto() throws IOException {
        BookingDtoRequest bookingDto = new BookingDtoRequest(
                1L,
                LocalDateTime.of(2023, 5, 22, 1, 34, 1),
                LocalDateTime.of(2023, 5, 23, 1, 34, 1)
        );

        JsonContent<BookingDtoRequest> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2023-05-22T01:34:01");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2023-05-23T01:34:01");
    }
}