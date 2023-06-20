package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    private User user;
    private User user2;
    private ItemRequestDto requestDto;
    private ItemRequest request;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.com");

        requestDto = new ItemRequestDto();
        requestDto.setDescription("Description");

        request = new ItemRequest(
                1L,
                "Description",
                user,
                LocalDateTime.now()
        );

        user2 = new User();
        user2.setId(2L);
        user2.setName("user");
        user2.setEmail("user@user.com");

        item = new Item();
        item.setName("Щётка для обуви");
        item.setDescription("Стандартная щётка для обуви");
        item.setAvailable(true);
        item.setOwner(user2);
        item.setRequest(request);
    }

    @Test
    void addNewRequestItemWithUserFoundThenReturnedItemRequest() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(request);

        ItemRequestDto actualRequest = requestService.addNewRequestItem(userId, requestDto);

        assertEquals(requestDto.getDescription(), actualRequest.getDescription());
        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void addNewRequestItemWithUserNotFoundThenReturnedThrow() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.addNewRequestItem(userId, requestDto));
        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getAllRequestItemByRequesterId() {
        long userId = 1L;
        Sort sort = Sort.by("created").descending();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterId(userId, sort)).thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(anySet())).thenReturn(List.of(item));

        Collection<ItemRequestInfo> actualList = requestService.getAllRequestItemByRequesterId(userId);

        assertEquals(1, actualList.size());
        verify(requestRepository, times(1)).findByRequesterId(userId, sort);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findByRequestIdIn(anySet());
    }

    @Test
    void getAllRequestItemByUserIdThenReturnedListRequest() {
        Integer from = 0;
        Integer size = 5;
        List<ItemRequest> list = List.of(request);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterIdNot(user.getId(), page)).thenReturn(new PageImpl<>(list));
        when(itemRepository.findByRequestIdIn(anySet())).thenReturn(List.of(item));

        Collection<ItemRequestInfo> actualList = requestService.getAllRequestItemByUserId(user.getId(), from, size);

        assertEquals(1, actualList.size());
        verify(requestRepository, times(1)).findByRequesterIdNot(user.getId(), page);
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findByRequestIdIn(anySet());
    }

    @Test
    void getAllRequestItemByUserIdWhenNotFoundThenReturnedEmptyListRequest() {
        Integer from = 0;
        Integer size = 5;
        List<ItemRequest> list = List.of(request);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterIdNot(user.getId(), page)).thenReturn(Page.empty());

        Collection<ItemRequestInfo> actualList = requestService.getAllRequestItemByUserId(user.getId(), from, size);

        assertTrue(actualList.isEmpty());
        verify(requestRepository, times(1)).findByRequesterIdNot(user.getId(), page);
    }

    @Test
    void getItemRequestByIdWhenRequestFoundThenReturnedRequest() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(request.getId())).thenReturn(List.of(item));

        ItemRequestInfo actualRequest = requestService.getItemRequestById(user2.getId(), request.getId());

        assertEquals(requestDto.getDescription(), actualRequest.getDescription());
        verify(userRepository, times(1)).findById(user2.getId());
        verify(itemRepository, times(1)).findByRequestId(request.getId());
        verify(requestRepository, times(1)).findById(request.getId());
    }

    @Test
    void getItemRequestByIdWhenRequestNotFoundThenReturnedThrow() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getItemRequestById(user2.getId(), request.getId()));
        verify(requestRepository, times(1)).findById(request.getId());
        verify(itemRepository, never()).findByRequestId(request.getId());
    }
}