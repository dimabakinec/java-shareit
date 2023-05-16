package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.Message.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemStorageImpl implements ItemStorage {

    private final UserStorage userStorage;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Long>> listItemsByIdUser = new HashMap<>();
    private long countId = 0L;

    private void validation(long userId, long itemId) {
        Item item = items.get(itemId);
        if (item.getOwner() != userId) {
            log.error(INVALID_USER_ID.getMessage(), userId);
            throw new NotFoundException(INVALID_USER_ID.getMessage() + userId);
        }
    }

    private void validationContainItem(long id) {
        if (!items.containsKey(id)) {
            log.error(MODEL_NOT_FOUND.getMessage() + id);
            throw new NotFoundException(MODEL_NOT_FOUND.getMessage() + id);
        }
    }

    private void dataValidator(String name) {
        if (name.isEmpty()) {
            log.error(NAME_MAY_NOT_CONTAIN_SPACES.getMessage());
            throw new ValidationException(NAME_MAY_NOT_CONTAIN_SPACES.getMessage());
        }
    }

    private void addItemToList(long id, Item item) {
        Set<Long> itemList = listItemsByIdUser.getOrDefault(id, new HashSet<>());
        itemList.add(item.getId());
        listItemsByIdUser.put(id, itemList);
    }

    @Override
    public Collection<ItemDto> getAllItemsByIdUser(long userId) {
        userStorage.findById(userId);
        return listItemsByIdUser.get(userId).stream()
                .map(items::get)
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemByID(long userId, long itemId) {
        userStorage.findById(userId);
        validationContainItem(itemId);
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public Collection<ItemDto> searchItem(long userId, String text) {
        userStorage.findById(userId);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userStorage.findById(userId);
        dataValidator(itemDto.getName());
        countId++;
        Item item = ItemMapper.toItem(countId, userId, itemDto);
        items.put(countId, item);
        addItemToList(userId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        userStorage.findById(userId);
        validationContainItem(itemId);
        validation(userId, itemId);
        Item item = items.get(itemId);
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();

        if (description != null) {
            item.setDescription(description);
        }
        if (name != null) {
            dataValidator(name);
            item.setName(name);
        }
        if (available != null) {
            item.setAvailable(available);
        }

        addItemToList(userId, item);
        items.put(itemId, item);
        return ItemMapper.toItemDto(item);
    }
}