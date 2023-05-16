package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemStorage {
    Collection<ItemDto> getAllItemsByIdUser(long userId);

    ItemDto getItemByID(long userId, long itemId);

    Collection<ItemDto> searchItem(long userId, String text);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);
}