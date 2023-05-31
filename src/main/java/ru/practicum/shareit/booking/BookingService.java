package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(Long userId, BookingDtoRequest bookingDtoRequest);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingByID(Long userId, Long bookingId);

    List<BookingDto> getAllBookingByUserId(Long userId, String state);

    List<BookingDto> getAllBookingByItemUser(Long userId, String state);
}