package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;

import java.util.Collection;

public interface ItemService {

    Collection<ItemInfo> getAllItemsByIdUser(long userId);

    ItemInfo getItem(long userId, long itemId);

    Collection<ItemDto> search(long userId, String text);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    CommentDtoResponse saveComment(Long userId, Long itemId, CommentDto commentDto);
}