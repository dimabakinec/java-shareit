package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto addNewRequestItem(Long userId, ItemRequestDto requestDto);

    Collection<ItemRequestInfo> getAllRequestItemByRequesterId(Long userId);

    ItemRequestInfo getItemRequestById(Long userId, Long requestId);

    Collection<ItemRequestInfo> getAllRequestItemByUserId(Long userId, Integer from, Integer size);
}