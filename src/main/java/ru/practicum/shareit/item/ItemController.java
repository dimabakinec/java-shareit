package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;

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

    @GetMapping
    public Collection<ItemInfo> getAllItemsByIdUser(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info(REQUEST_ALL.getMessage());
        return itemService.getAllItemsByIdUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemInfo getItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId) {
        log.info(REQUEST_BY_ID.getMessage(), itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                      @RequestParam String text) {
        log.info(SEARCH.getMessage());
        return itemService.search(userId,text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info(ADD_MODEL.getMessage(), itemDto);
        return itemService.create(userId,itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info(UPDATED_MODEL.getMessage(), itemId, itemDto);
        return itemService.update(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId,
                                         @Valid @RequestBody CommentDto commentDto) {
        log.info(ADD_MODEL.getMessage(), commentDto);
        CommentDtoResponse commentDtoResponse = itemService.saveComment(userId, itemId, commentDto);
        log.info(ADD_MODEL.getMessage(), commentDtoResponse);
        return commentDtoResponse;
    }
}