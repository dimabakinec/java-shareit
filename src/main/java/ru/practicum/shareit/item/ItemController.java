package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collection;

import static ru.practicum.shareit.utils.Message.*;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    private void validateUserId(long userId) {
        if (userId == 0) {
            log.error(INVALID_USER_ID.getMessage(), userId);
            throw new NotFoundException(INVALID_USER_ID.getMessage() + userId);
        }
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByIdUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info(REQUEST_ALL.getMessage());
        return itemService.getAllItemsByIdUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info(REQUEST_BY_ID.getMessage(), itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam String text) {
        log.info(SEARCH.getMessage());
        return itemService.search(userId,text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        validateUserId(userId);
        log.info(ADD_MODEL.getMessage(), itemDto);
        return itemService.create(userId,itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        validateUserId(userId);
        log.info(UPDATED_MODEL.getMessage(), itemId, itemDto);
        return itemService.update(userId, itemId, itemDto);
    }
}