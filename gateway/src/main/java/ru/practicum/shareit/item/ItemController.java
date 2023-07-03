package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient client;

    @GetMapping
    public ResponseEntity<Object> getAllItemsByIdUser(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                      @RequestParam(value = "from",
                                                            defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(value = "size",
                                                            defaultValue = "10") @Positive Integer size) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return client.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId) {
        log.info("Get item {}, userId {}", itemId, userId);
        return client.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                      @RequestParam String text,
                                      @RequestParam(value = "from",
                                              defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(value = "size",
                                              defaultValue = "10") @Positive Integer size) {
        log.info("Get item with text {}, userId={}, from={}, size={}", text, userId, from, size);
        return client.searchItem(userId,text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return client.createItem(userId,itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Updating item {}, itemId={}, userId={}", itemDto, itemId, userId);
        return client.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId,
                                         @Valid @RequestBody CommentRequestDto commentDto) {
        log.info("Creating comment {}, itemId={}, userId={}", commentDto, itemId, userId);
        return client.createComment(userId, itemId, commentDto);
    }
}