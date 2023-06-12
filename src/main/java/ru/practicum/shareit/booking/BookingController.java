package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static ru.practicum.shareit.utils.Message.*;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info(ADD_MODEL.getMessage(), bookingDtoRequest);
        return bookingService.addNewBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam Boolean approved) {
        log.info(UPDATED_MODEL.getMessage(), bookingId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        log.info(REQUEST_BY_ID.getMessage(), bookingId);
        return bookingService.getBookingByID(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsByIdUser(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state,
                                                         @RequestParam(value = "from",
                                                                 defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(value = "size",
                                                                 defaultValue = "10") @Positive Integer size) {
        log.info(REQUEST_ALL.getMessage());
        return bookingService.getAllBookingByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingAllItemByIdUser(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                               @RequestParam(defaultValue = "ALL") String state,
                                                               @RequestParam(value = "from",
                                                                       defaultValue = "0") @PositiveOrZero Integer from,
                                                               @RequestParam(value = "size",
                                                                       defaultValue = "10") @Positive Integer size) {
        log.info(REQUEST_ALL.getMessage());
        return bookingService.getAllBookingByOwnerItem(userId, state, from, size);
    }
}