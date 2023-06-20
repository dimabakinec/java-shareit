package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class ItemMapper {

    public static Item toNewItem(User user, ItemDto itemDto, ItemRequest request) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequest(request);

        return item;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public static ItemInfo toGetItemWithBooking(Item item, List<Booking> bookingList, List<Comment> commentList) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.setId(item.getId());
        itemInfo.setName(item.getName());
        itemInfo.setDescription(item.getDescription());
        itemInfo.setAvailable(item.getAvailable());

        if (!bookingList.isEmpty()) {
            LocalDateTime nowDate = LocalDateTime.now();
            Optional<Booking> lastBooking = bookingList.stream()
                    .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                    .filter(booking -> booking.getStart().isBefore(nowDate))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .findFirst();

            Optional<Booking> nextBooking = bookingList.stream()
                    .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                    .filter(booking -> booking.getStart().isAfter(nowDate))
                    .findFirst();

            if (lastBooking.isPresent()) {
                Booking last = lastBooking.get();
                itemInfo.setLastBooking(new BookingDtoForItem(last.getId(),
                        last.getStart(), last.getEnd(), last.getBooker().getId()));
            }
            if (nextBooking.isPresent()) {
                Booking next = nextBooking.get();
                itemInfo.setNextBooking(new BookingDtoForItem(next.getId(), next.getStart(),
                        next.getEnd(), next.getBooker().getId()));
            }
        }

        List<CommentDtoResponse> commentDtoResponses = commentList.stream().map(CommentMapper::mapToCommentDtoResponse)
                .collect(toList());
        itemInfo.setComments(new HashSet<>(commentDtoResponses));
        return itemInfo;
    }
}