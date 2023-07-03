package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                  @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Creating itemRequest {}, userId={}", requestDto, userId);
        return requestClient.createItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestByIdRequester(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Get requests by userId {}", userId);
        return requestClient.getItemRequestByRequesterId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                         @RequestParam(value = "from",
                                                                 defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(value = "size",
                                                                 defaultValue = "10") @Positive Integer size) {
        log.info("Get requests with userId={}, from={}, size={}", userId, from, size);
        return requestClient.getItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                              @PathVariable Long requestId) {
        log.info("Get itemRequest {}, userId {}", requestId, userId);
        return requestClient.getItemRequestById(userId, requestId);
    }
}