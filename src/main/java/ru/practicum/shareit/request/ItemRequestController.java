package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static ru.practicum.shareit.utils.Message.*;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto saveItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemRequestDto requestDto) {
        log.info(ADD_MODEL.getMessage(), requestDto);
        ItemRequestDto requestDtoNew = requestService.addNewRequestItem(userId, requestDto);
        log.info(ADD_MODEL.getMessage(), requestDtoNew);
        return requestDtoNew;
    }

    @GetMapping
    public Collection<ItemRequestInfo> getItemRequestByIdRequester(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info(REQUEST_BY_USER_ID.getMessage(), userId);
        return requestService.getAllRequestItemByRequesterId(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestInfo> getAllItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                         @RequestParam(value = "from",
                                                                 defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(value = "size",
                                                                 defaultValue = "10") @Positive Integer size) {
        log.info(REQUEST_ALL.getMessage());
        return requestService.getAllRequestItemByUserId(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfo getItemRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        log.info(REQUEST_BY_ID.getMessage(), requestId);
        return requestService.getItemRequestById(userId, requestId);
    }
}