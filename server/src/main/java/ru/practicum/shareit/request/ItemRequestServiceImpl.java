package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.*;
import static ru.practicum.shareit.utils.Message.ADD_MODEL;
import static ru.practicum.shareit.utils.Message.MODEL_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User validUser(Long id) {
        return userRepository.findById(id).stream().findAny()
                .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + id));
    }

    @Override
    @Transactional
    public ItemRequestDto addNewRequestItem(Long userId, ItemRequestDto requestDto) {
        User user = validUser(userId);
        ItemRequest request = requestRepository.save(mapToNewItemRequest(user, requestDto));
        log.info(ADD_MODEL.getMessage(), request);
        return mapToItemRequestDto(request);
    }

    @Override
    public Collection<ItemRequestInfo> getAllRequestItemByRequesterId(Long userId) {
        validUser(userId);
        Map<Long, ItemRequest> requestMap = requestRepository.findByRequesterId(userId,
                        Sort.by("created").descending()).stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        Map<Long, List<Item>> itemByRequest = itemRepository.findByRequestIdIn(requestMap.keySet()).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        return requestMap.values().stream()
                .map(itemRequest -> mapToItemRequestInfo(
                       itemRequest,
                       itemByRequest.getOrDefault(itemRequest.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestInfo> getAllRequestItemByUserId(Long userId, Integer from, Integer size) {
        validUser(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        Map<Long, ItemRequest> requestMap = requestRepository.findByRequesterIdNot(userId, page).stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        Map<Long, List<Item>> itemByRequest = itemRepository.findByRequestIdIn(requestMap.keySet()).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        return requestMap.values().stream()
                .map(itemRequest -> mapToItemRequestInfo(
                        itemRequest,
                        itemByRequest.getOrDefault(itemRequest.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestInfo getItemRequestById(Long userId, Long requestId) {
        validUser(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + requestId));
        List<Item> itemList = itemRepository.findByRequestId(requestId);
        return mapToItemRequestInfo(request, itemList);
    }
}