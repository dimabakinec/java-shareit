package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingStatus.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl service;
    private User user;
    private User user2;
    private Item item;
    private Booking booking;
    private BookingDtoRequest bookingDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.com");
        user2 = new User();
        user2.setId(2L);
        user2.setName("user");
        user2.setEmail("user@user.com");

        item = new Item();
        item.setId(1L);
        item.setName("Щётка для обуви");
        item.setDescription("Стандартная щётка для обуви");
        item.setAvailable(true);
        item.setOwner(user);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 5, 22, 0, 0));
        booking.setEnd(LocalDateTime.of(2023, 5, 23, 0, 0));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user2);

        bookingDto = new BookingDtoRequest(
                1L,
                LocalDateTime.of(2023, 5, 22, 0, 0),
                LocalDateTime.of(2023, 5, 23, 0, 0)
        );
    }

    @Test
    void addNewBookingWhenUserEqualsOwnerItemThenReturnedException() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> service.addNewBooking(user2.getId(), bookingDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addNewBookingWhenNotAvailableThenReturnedException() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> service.addNewBooking(user2.getId(), bookingDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addNewBookingWhenUserNotEqualsOwnerItemThenReturnedBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto actual = service.addNewBooking(user2.getId(), bookingDto);

        assertEquals(actual.getStart(), booking.getStart());
        assertEquals(actual.getEnd(), bookingDto.getEnd());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void addNewBookingWhenInvalidDateThenReturnedException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        bookingDto = new BookingDtoRequest(
                1L,
                LocalDateTime.of(2023, 5, 23, 0, 0),
                LocalDateTime.of(2023, 5, 22, 0, 0)
        );

        assertThrows(ValidationException.class, () -> service.addNewBooking(user2.getId(), bookingDto));
        verify(bookingRepository, never()).save(any());

        bookingDto = new BookingDtoRequest(
                1L,
                LocalDateTime.of(2023, 5, 22, 0, 0),
                LocalDateTime.of(2023, 5, 22, 0, 0)
        );

        assertThrows(ValidationException.class, () -> service.addNewBooking(user2.getId(), bookingDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingWhenUserOwnerItemThenUpdateAndReturnedBooking() {
        when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        Boolean approved = true;
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto actual = service.updateBooking(user.getId(), booking.getId(), approved);

        assertEquals(APPROVED, actual.getStatus());
    }

    @Test
    void updateBookingWhenApprovedFalseThenUpdateAndReturnedBooking() {
        when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        Boolean approved = false;
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto actual = service.updateBooking(user.getId(), booking.getId(), approved);

        assertEquals(REJECTED, actual.getStatus());
    }

    @Test
    void updateBookingWhenApprovedStatusBookingAndApprovedTrueThenReturnedException() {
        booking.setStatus(APPROVED);
        when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        Boolean approved = true;
        assertThrows(ValidationException.class, () -> service.updateBooking(user.getId(), booking.getId(), approved));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingWhenNotFoundBookingThenReturnedException() {
        Boolean approved = true;
        when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateBooking(user2.getId(), booking.getId(), approved));
    }

    @Test
    void getBookingByID() {
        when(bookingRepository.findByIdAndBookerIdOrItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));

        BookingDto actual = service.getBookingByID(user.getId(), booking.getId());

        assertEquals(booking.getId(), actual.getId());
    }

    @Test
    void getBookingByIdWhenUnknownUserThenReturnException() {
        long userId = 3L;
        when(bookingRepository.findByIdAndBookerIdOrItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getBookingByID(userId, booking.getId()));
    }

    @Test
    void getAllBookingByUserIdWhenUserBookerThenReturnedBookingOrException() {
        Integer from = 0;
        Integer size = 5;
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(LocalDateTime.of(2023, 5, 24, 0, 0));
        booking2.setEnd(LocalDateTime.of(2023, 5, 25, 0, 0));
        booking2.setStatus(APPROVED);
        booking2.setItem(item);
        booking2.setBooker(user2);
        List<Booking> listBooking = List.of(booking, booking2);
        List<Booking> listBooking2 = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        // status WAITING
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(listBooking2));

        List<BookingDto> actualList = service.getAllBookingByUserId(user2.getId(), "WAITING", from, size);

        assertEquals(1, actualList.size());
        assertEquals(booking.getId(), actualList.get(0).getId());

        // status ALL
        when(bookingRepository.findByBookerId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(listBooking));

        actualList = service.getAllBookingByUserId(user2.getId(), "all", from, size);

        assertEquals(2, actualList.size());
        assertEquals(2L, actualList.get(1).getId());

        // status UNSUPPORTED_STATUS
        assertThrows(ValidationException.class, () -> service.getAllBookingByUserId(user2.getId(),
                "UNSUPPORTED_STATUS", from, size));

        // status CURRENT
        when(bookingRepository.findByBookerIdAndCurrentMomentBetweenStartAndEnd(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(new PageImpl<>(listBooking2));

        actualList = service.getAllBookingByUserId(user2.getId(), "CURRENT", from, size);

        assertEquals(1, actualList.size());
        assertEquals(booking.getId(), actualList.get(0).getId());
    }

    @Test
    void getAllBookingByItemUser() {
        Integer from = 0;
        Integer size = 5;
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(LocalDateTime.of(2023, 5, 24, 0, 0));
        booking2.setEnd(LocalDateTime.of(2023, 5, 25, 0, 0));
        booking2.setStatus(APPROVED);
        booking2.setItem(item);
        booking2.setBooker(user2);
        List<Booking> listBooking = List.of(booking, booking2);
        List<Booking> listBooking2 = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // status WAITING
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(listBooking2));

        List<BookingDto> actualList = service.getAllBookingByOwnerItem(user.getId(), "WAITING", from, size);

        assertEquals(1, actualList.size());
        assertEquals(booking.getId(), actualList.get(0).getId());

        // status ALL
        when(bookingRepository.findByItemOwnerId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(listBooking));

        actualList = service.getAllBookingByOwnerItem(user2.getId(), "all", from, size);

        assertEquals(2, actualList.size());
        assertEquals(2L, actualList.get(1).getId());

        // status UNSUPPORTED_STATUS
        assertThrows(ValidationException.class, () -> service.getAllBookingByOwnerItem(user.getId(),
                "UNSUPPORTED_STATUS", from, size));

        // status CURRENT
        when(bookingRepository.findByItemOwnerIdAndCurrentMomentBetweenStartAndEnd(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(new PageImpl<>(listBooking2));

        actualList = service.getAllBookingByOwnerItem(user.getId(), "CURRENT", from, size);

        assertEquals(1, actualList.size());
        assertEquals(booking.getId(), actualList.get(0).getId());
    }
}