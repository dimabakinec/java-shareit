package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = { "db.name=test"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingService service;
    private User userResult1;
    private User userResult2;
    private Booking bookingResult;
    private Booking bookingResult2;
    private Booking bookingResult3;
    private final int from = 0;
    private final int size = 5;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.com");
        userResult1 = userRepository.save(user);

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user.com");
        userResult2 = userRepository.save(user2);

        Item item = new Item();
        item.setName("Щётка для обуви");
        item.setDescription("Стандартная щётка для обуви");
        item.setAvailable(true);
        item.setOwner(userResult1);
        Item itemResult = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(itemResult);
        booking.setBooker(userResult2);
        bookingResult = bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setItem(itemResult);
        booking2.setBooker(userResult2);
        bookingResult2 = bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStart(LocalDateTime.now().plusDays(2));
        booking3.setEnd(LocalDateTime.now().plusDays(3));
        booking3.setStatus(BookingStatus.REJECTED);
        booking3.setItem(itemResult);
        booking3.setBooker(userResult2);
        bookingResult3 = bookingRepository.save(booking3);
    }

    @Test
    void getAllBookingByUserIdWhenUserNotFoundThenReturnedException() {
        String status = "all";
        long userId = 0L;

        assertThrows(NotFoundException.class, () -> service.getAllBookingByUserId(userId, status, from, size));
    }

    @Test
    void getAllBookingByUserIdWhenStateAllFoundThenReturnedList() {
        String status = "all";

        List<BookingDto> resultList = service.getAllBookingByUserId(userResult2.getId(), status, from, size);

        assertEquals(3, resultList.size());
    }

    @Test
    void getAllBookingByUserIdWhenSize2FoundThenReturnedList() {
        String status = "all";
        int size2 = 2;

        List<BookingDto> resultList = service.getAllBookingByUserId(userResult2.getId(), status, from, size2);

        assertEquals(2, resultList.size());
        assertEquals(bookingResult3.getId(), resultList.get(0).getId());
        assertEquals(bookingResult2.getId(), resultList.get(1).getId());
    }

    @Test
    void getAllBookingByUserIdWhenStateWaitFoundThenReturnedList() {
        String status = "WAITING";

        List<BookingDto> resultList = service.getAllBookingByUserId(userResult2.getId(), status, from, size);

        assertEquals(1, resultList.size());
        assertEquals(bookingResult2.getId(), resultList.get(0).getId());
    }

    @Test
    void getAllBookingByUserIdWhenStateCurrentFoundThenReturnedList() {
        String status = "Current";

        List<BookingDto> resultList = service.getAllBookingByUserId(userResult2.getId(), status, from, size);

        assertEquals(1, resultList.size());
        assertEquals(bookingResult2.getId(), resultList.get(0).getId());
    }

    @Test
    void getAllBookingByUserIdWhenStatePastFoundThenReturnedList() {
        String status = "Past";

        List<BookingDto> resultList = service.getAllBookingByUserId(userResult2.getId(), status, from, size);

        assertEquals(1, resultList.size());
        assertEquals(bookingResult.getId(), resultList.get(0).getId());
    }

    @Test
    void getAllBookingByUserIdWhenStateRejectedFoundThenReturnedList() {
        String status = "Rejected";

        List<BookingDto> resultList = service.getAllBookingByUserId(userResult2.getId(), status, from, size);

        assertEquals(1, resultList.size());
        assertEquals(bookingResult3.getId(), resultList.get(0).getId());

        String status2 = "Future";

        List<BookingDto> resultList2 = service.getAllBookingByUserId(userResult2.getId(), status2, from, size);

        assertEquals(1, resultList2.size());
        assertEquals(bookingResult3.getId(), resultList2.get(0).getId());
    }

    @Test
    void getAllBookingByUserIdWhenUnknownStateThenReturnedException() {
        String status = "UNSUPPORTED_STATUS";

        assertThrows(ValidationException.class, () -> service.getAllBookingByUserId(userResult2.getId(), status, from, size));
    }

    @Test
    void getAllBookingByUserIdWhenNotFoundBookingThenReturnedEmptyList() {
        String status = "all";

        List<BookingDto> resultList = service.getAllBookingByUserId(userResult1.getId(), status, from, size);

        assertTrue(resultList.isEmpty());
    }
}