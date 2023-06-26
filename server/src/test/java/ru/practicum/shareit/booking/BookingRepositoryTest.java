package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;
    private PageRequest page;
    private User user;
    private Item item;
    private Item item2;
    private User userBooker;
    private Booking booking;

    @BeforeEach
    void before() {
        user = new User();
        user.setName("user");
        user.setEmail("user@user.com");

        item = new Item();
        item.setName("Щётка для обуви");
        item.setDescription("Стандартная щётка для обуви");
        item.setAvailable(true);
        item.setOwner(user);

        item2 = new Item();
        item2.setName("Краска для обуви");
        item2.setDescription("Стандартная краска для обуви");
        item2.setAvailable(true);
        item2.setOwner(user);

        userBooker = new User();
        userBooker.setName("userBooker");
        userBooker.setEmail("userBooker@user.com");

        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);
        userRepository.save(userBooker);

        booking = new Booking();
        booking.setStart(LocalDateTime.of(2023, 5, 22, 1, 34, 1));
        booking.setEnd(LocalDateTime.of(2023, 5, 23, 1, 34, 1));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item2);
        booking.setBooker(userBooker);

        repository.save(booking);

        int from = 0;
        int size = 10;
        page = PageRequest.of(from > 0 ? from / size : 0, size);
    }

    @AfterEach
    void after() {
        repository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByIdAndItemOwnerId() {
        Optional<Booking> findBooking = repository.findByIdAndItemOwnerId(booking.getId(), user.getId());

        assertTrue(findBooking.isPresent());
    }

    @Test
    void findByIdAndItemOwnerIdWhenOwnerIdInvalidThenEmpty() {
        Optional<Booking> findBooking = repository.findByIdAndItemOwnerId(booking.getId(), userBooker.getId());

        assertTrue(findBooking.isEmpty());
    }

    @Test
    void findByIdAndBookerIdOrItemOwnerId() {
        Optional<Booking> findBooking = repository.findByIdAndBookerIdOrItemOwnerId(booking.getId(), userBooker.getId());

        assertTrue(findBooking.isPresent());
    }

    @Test
    void findByIdAndInvalidBookerIdOrItemOwnerIdThenReturnedEmptyList() {
        Optional<Booking> findBooking = repository.findByIdAndBookerIdOrItemOwnerId(booking.getId(), 3L);

        assertTrue(findBooking.isEmpty());
    }

    @Test
    void findByBookerIdAndCurrentMomentBetweenStartAndEnd() {
        List<Booking> bookingList = repository.findByBookerIdAndCurrentMomentBetweenStartAndEnd(userBooker.getId(),
                LocalDateTime.of(2023, 5, 22, 3, 34, 1), page).getContent();

        assertEquals(1, bookingList.size(), "Размер списка не равен 1");
        assertEquals(booking.getId(), bookingList.get(0).getId(), "Значения не равны");
    }

    @Test
    void findByInvalidBookerIdAndCurrentMomentBetweenStartAndEndThenReturnedEmptyList() {
        List<Booking> bookingList = repository.findByBookerIdAndCurrentMomentBetweenStartAndEnd(user.getId(),
                LocalDateTime.of(2023, 5, 22, 1, 34, 1), page).getContent();

        assertTrue(bookingList.isEmpty());
    }

    @Test
    void findByItemOwnerIdAndCurrentMomentBetweenStartAndEnd() {
        List<Booking> bookingList = repository.findByItemOwnerIdAndCurrentMomentBetweenStartAndEnd(user.getId(),
                LocalDateTime.of(2023, 5, 22, 3, 34, 1), page).getContent();

        assertEquals(1, bookingList.size(), "Размер списка не равен 1");
        assertEquals(booking.getId(), bookingList.get(0).getId(), "Значения не равны");
    }

    @Test
    void findByItemOwnerIdAndCurrentMomentBetweenStartAndEndThenReturnedEmptyList() {
        List<Booking> bookingList = repository.findByItemOwnerIdAndCurrentMomentBetweenStartAndEnd(userBooker.getId(),
                LocalDateTime.of(2023, 5, 22, 1, 34, 1), page).getContent();

        assertTrue(bookingList.isEmpty());
    }

    @Test
    void getBookingDateThenReturnedList() {
        List<Booking> bookingList = repository.getBookingDate(item2.getId(),
                LocalDateTime.of(2023, 5, 21, 1, 34, 1),
                LocalDateTime.of(2023, 5, 25, 1, 34, 1));

        assertEquals(1, bookingList.size(), "Размер списка не равен 1");
        assertEquals(booking.getId(), bookingList.get(0).getId(), "Значения не равны");
    }

    @Test
    void findByItemIdAndBookerIdAndStatusAndEndBeforeWhenBookingStatusApprovedThenReturnedBookingOrEmpty() {
        List<Booking> bookingList = repository.findByItemIdAndBookerIdAndStatusAndEndBefore(item2.getId(),
                userBooker.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        assertTrue(bookingList.isEmpty());

        booking.setStatus(BookingStatus.APPROVED);
        repository.save(booking);
        bookingList = repository.findByItemIdAndBookerIdAndStatusAndEndBefore(item2.getId(),
                userBooker.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        assertEquals(1, bookingList.size());
        assertEquals(booking.getId(), bookingList.get(0).getId());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());

        bookingList = repository.findByItemIdAndBookerIdAndStatusAndEndBefore(item2.getId(),
                user.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        assertTrue(bookingList.isEmpty());
    }

    @Test
    void findByBookerIdWhenFoundBookingOrNotFoundThenReturnedBookingOrEmpty() {
        Page<Booking> bookingList = repository.findByBookerId(userBooker.getId(), page);

        assertTrue(bookingList.isFirst());

        bookingList = repository.findByBookerId(user.getId(), page);

        assertTrue(bookingList.isEmpty());
    }

    @Test
    void findByBookerIdAndEndBeforeWhenFoundBookingOrNotFoundThenReturnedBookingOrEmpty() {
        Page<Booking> bookingList = repository.findByBookerIdAndEndBefore(userBooker.getId(),
                LocalDateTime.of(2023, 5, 22, 1, 34, 1), page);

        assertTrue(bookingList.isEmpty());

        bookingList = repository.findByBookerIdAndEndBefore(userBooker.getId(), LocalDateTime.now(), page);

        assertTrue(bookingList.isFirst());
    }

    @Test
    void findByBookerIdAndStartAfterWhenFoundBookingOrNotFoundThenReturnedBookingOrEmpty() {
        Page<Booking> bookingList = repository.findByBookerIdAndStartAfter(userBooker.getId(),
                LocalDateTime.of(2023, 5, 21, 1, 34, 1), page);

        assertTrue(bookingList.isFirst());


        bookingList = repository.findByBookerIdAndStartAfter(userBooker.getId(), LocalDateTime.now(), page);

        assertTrue(bookingList.isEmpty());
    }

    @Test
    void findByBookerIdAndStatusWaiting() {
        List<Booking> bookingList = repository.findByBookerIdAndStatus(userBooker.getId(), WAITING, page).getContent();

        assertEquals(1, bookingList.size());
        assertEquals(booking.getId(), bookingList.get(0).getId());
        assertEquals(WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findByBookerIdAndStatusRejected() {
        List<Booking> bookingList = repository.findByBookerIdAndStatus(userBooker.getId(), REJECTED, page).getContent();

        assertTrue(bookingList.isEmpty());

        booking.setStatus(REJECTED);
        repository.save(booking);

        bookingList = repository.findByBookerIdAndStatus(userBooker.getId(), REJECTED, page).getContent();

        assertEquals(1, bookingList.size());
        assertEquals(booking.getId(), bookingList.get(0).getId());
        assertEquals(REJECTED, bookingList.get(0).getStatus());

        bookingList = repository.findByBookerIdAndStatus(user.getId(), REJECTED, page).getContent();

        assertTrue(bookingList.isEmpty());
    }

    @Test
    void findByItemOwnerIdWhenFoundBookingOrNotFoundThenReturnedBookingOrEmpty() {
        Page<Booking> bookingList = repository.findByItemOwnerId(user.getId(), page);

        assertTrue(bookingList.isFirst());

        bookingList = repository.findByItemOwnerId(userBooker.getId(), page);

        assertTrue(bookingList.isEmpty());
    }

    @Test
    void findByItemOwnerIdAndEndBeforeWhenFoundBookingOrNotFoundThenReturnedBookingOrEmpty() {
        Page<Booking> bookingList = repository.findByItemOwnerIdAndEndBefore(user.getId(),
                LocalDateTime.of(2023, 5, 22, 1, 34, 1), page);

        assertTrue(bookingList.isEmpty());

        bookingList = repository.findByItemOwnerIdAndEndBefore(user.getId(), LocalDateTime.now(), page);

        assertTrue(bookingList.isFirst());
    }

    @Test
    void findByItemOwnerIdAndStartAfterWhenFoundBookingOrNotFoundThenReturnedBookingOrEmpty() {
        Page<Booking> bookingList = repository.findByItemOwnerIdAndStartAfter(user.getId(),
                LocalDateTime.of(2023, 5, 21, 1, 34, 1), page);

        assertTrue(bookingList.isFirst());


        bookingList = repository.findByItemOwnerIdAndStartAfter(user.getId(), LocalDateTime.now(), page);

        assertTrue(bookingList.isEmpty());
    }

    @Test
    void findByItemOwnerIdAndStatusWaiting() {
        List<Booking> bookingList = repository.findByItemOwnerIdAndStatus(user.getId(), WAITING, page).getContent();

        assertEquals(1, bookingList.size());
        assertEquals(booking.getId(), bookingList.get(0).getId());
        assertEquals(WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findByItemOwnerIdAndStatusRejected() {
        List<Booking> bookingList = repository.findByItemOwnerIdAndStatus(user.getId(), REJECTED, page).getContent();

        assertTrue(bookingList.isEmpty());

        booking.setStatus(REJECTED);
        repository.save(booking);

        bookingList = repository.findByItemOwnerIdAndStatus(user.getId(), REJECTED, page).getContent();

        assertEquals(1, bookingList.size());
        assertEquals(booking.getId(), bookingList.get(0).getId());
        assertEquals(REJECTED, bookingList.get(0).getStatus());

        bookingList = repository.findByItemOwnerIdAndStatus(userBooker.getId(), REJECTED, page).getContent();

        assertTrue(bookingList.isEmpty());
    }
}