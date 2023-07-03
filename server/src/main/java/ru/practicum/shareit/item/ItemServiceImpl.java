package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.mapper.CommentMapper.mapToCommentDtoResponse;
import static ru.practicum.shareit.item.mapper.CommentMapper.mapToNewComment;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.utils.Message.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    private User validUser(Long id) {
        return userRepository.findById(id).stream().findAny()
                .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + id));
    }

    @Override
    public Collection<ItemInfo> getAllItemsByIdUser(long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        Map<Long, Item> itemMap = itemRepository.findByOwnerId(userId, page)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, List<Booking>> bookingMap = bookingRepository.findByItemIdIn(itemMap.keySet(),
                        Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Comment>> comments = commentRepository.findByItemIdIn(itemMap.keySet(),
                Sort.by(Sort.Direction.ASC, "created")).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        return itemMap.values()
                .stream()
                .map(item -> toGetItemWithBooking(
                        item,
                        bookingMap.getOrDefault(item.getId(), Collections.emptyList()),
                        comments.getOrDefault(item.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ItemInfo getItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + itemId));
        List<Booking> bookingList = new ArrayList<>();

        if (item.getOwner().getId() == userId) {
            bookingList = bookingRepository.findByItemIdAndStatus(itemId,
                    BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"));

        }

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedAsc(itemId);

        return toGetItemWithBooking(item, bookingList, comments);
    }

    @Override
    public Collection<ItemDto> search(long userId, String text, Integer from, Integer size) {
        validUser(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Transactional
    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = validUser(userId);
        Long requestId = itemDto.getRequestId();
        ItemRequest request = null;
        if (requestId != null) {
            request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + requestId));
        }
        Item item = itemRepository.save(toNewItem(user, itemDto, request));
        log.info(ADD_MODEL.getMessage(), item);
        return toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        validUser(userId);
        Item item = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + itemId));
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();

        if (description != null) {
            item.setDescription(description);
        }
        if (name != null) {
            item.setName(name);
        }
        if (available != null) {
            item.setAvailable(available);
        }

        return toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public CommentDtoResponse saveComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = validUser(userId);
        List<Booking> bookingList = bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId,
                BookingStatus.APPROVED, commentDto.getCreated());
        if (bookingList.isEmpty()) {
            throw new ValidationException(NOT_ADD_COMMENT.getMessage());
        }
        Item item = itemRepository.findById(itemId).stream().findAny()
                .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + itemId));

        Comment comment = commentRepository.save(mapToNewComment(user, item, commentDto));

        return mapToCommentDtoResponse(comment);
    }
}