package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingState.valueOfIgnoreCase;
import static ru.practicum.shareit.booking.BookingStatus.*;
import static ru.practicum.shareit.utils.Message.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User validUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + userId));
    }

    private Item validItemById(Long itemId) {
         return itemRepository.findById(itemId)
                 .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + itemId));
    }

    private void validStartEndEndDate(BookingDtoRequest bookingDtoRequest) {
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new ValidationException(INVALID_DATE.getMessage());
        }
        if (bookingDtoRequest.getEnd().isEqual(bookingDtoRequest.getStart())) {
            throw new ValidationException(INVALID_DATE.getMessage());
        }
        List<Booking> bookingList = bookingRepository.getBookingDate(bookingDtoRequest.getItemId(),
                bookingDtoRequest.getStart(), bookingDtoRequest.getEnd());
        if (bookingList.size() > 0) {
            throw new ValidationException(INVALID_DATE.getMessage());
        }

    }

    @Transactional
    @Override
    public BookingDto addNewBooking(Long userId, BookingDtoRequest bookingDtoRequest) {
        User user = validUserById(userId);
        Item item = validItemById(bookingDtoRequest.getItemId());
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException(IS_OWNER_ITEM.getMessage());
        }
        if (!item.getAvailable()) {
            throw new ValidationException(NOT_AVAILABLE.getMessage());
        }
        validStartEndEndDate(bookingDtoRequest);
        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(user, item, bookingDtoRequest));
        log.info(ADD_MODEL.getMessage(), booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(INVALID_USER_REQUEST_APPROVED.getMessage()));

        if (approved && booking.getStatus() == APPROVED) {
            throw new ValidationException(BEEN_APPROVED.getMessage());
        }
        if (approved) {
            booking.setStatus(APPROVED);
        }
        if (!approved && booking.getStatus() != REJECTED) {
            booking.setStatus(REJECTED);
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingByID(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdAndBookerIdOrItemOwnerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + bookingId));
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingByUserId(Long userId, String state) {
        validUserById(userId);
        List<Booking> bookingList;
        LocalDateTime currentMoment = LocalDateTime.now();
        BookingState status = valueOfIgnoreCase(state);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        switch (status) {
            case ALL:
                bookingList = bookingRepository.findByBookerId(userId, sort);
                break;
            case PAST:
                bookingList = bookingRepository.findByBookerIdAndEndBefore(userId, currentMoment, sort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdAndStartAfter(userId, currentMoment, sort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdAndCurrentMomentBetweenStartAndEnd(userId, currentMoment);
                break;
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatus(userId, WAITING, sort);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatus(userId, REJECTED, sort);
                break;
            default:
                throw new ValidationException(UNKNOWN_STATE.getMessage());
        }
        return bookingList.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingByItemUser(Long userId, String state) {
        validUserById(userId);
        List<Booking> bookingList;
        LocalDateTime currentMoment = LocalDateTime.now();
        BookingState status = valueOfIgnoreCase(state);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        switch (status) {
            case ALL:
                bookingList = bookingRepository.findByItemOwnerId(userId, sort);
                break;
            case PAST:
                bookingList = bookingRepository.findByItemOwnerIdAndEndBefore(userId, currentMoment, sort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByItemOwnerIdAndStartAfter(userId, currentMoment, sort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByItemOwnerIdAndCurrentMomentBetweenStartAndEnd(userId, currentMoment);
                break;
            case WAITING:
                bookingList = bookingRepository.findByItemOwnerIdAndStatus(userId, WAITING, sort);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByItemOwnerIdAndStatus(userId, REJECTED, sort);
                break;
            default:
                throw new ValidationException(UNKNOWN_STATE.getMessage());
        }
        return bookingList.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }
}