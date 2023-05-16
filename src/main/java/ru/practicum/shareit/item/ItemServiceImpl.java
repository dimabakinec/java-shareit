package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public Collection<ItemDto> getAllItemsByIdUser(long userId) {
        return itemStorage.getAllItemsByIdUser(userId);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        return itemStorage.getItemByID(userId, itemId);
    }

    @Override
    public Collection<ItemDto> search(long userId, String text) {
        return itemStorage.searchItem(userId, text);
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        return itemStorage.createItem(userId, itemDto);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        return itemStorage.updateItem(userId, itemId, itemDto);
    }
}