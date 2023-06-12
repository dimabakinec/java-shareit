package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest mapToNewItemRequest(User user, ItemRequestDto requestDto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequester(user);

        return request;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated()
        );
    }

    public static ItemRequestInfo mapToItemRequestInfo(ItemRequest request, List<Item> itemsList) {
        ItemRequestInfo requestInfo = new ItemRequestInfo();
        requestInfo.setId(request.getId());
        requestInfo.setDescription(request.getDescription());
        requestInfo.setCreated(request.getCreated());

        List<ItemDto> items = itemsList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        requestInfo.setItems(new HashSet<>(items));
        return requestInfo;
    }
}