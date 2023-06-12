package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.toNewItem;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User user;
    private User user2;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest request;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.com");
        user2 = new User();
        user2.setId(2L);
        user2.setName("user");
        user2.setEmail("user@user.com");
        request = new ItemRequest(
                1L,
                "Description",
                user,
                LocalDateTime.of(2023, 5, 22, 0, 0)
        );

        item = new Item();
        item.setId(1L);
        item.setName("Щётка для обуви");
        item.setDescription("Стандартная щётка для обуви");
        item.setAvailable(true);
        item.setOwner(user);


        booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user2);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment");
        comment.setAuthor(user2);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 5, 22, 1, 34, 1));

        itemDto = new ItemDto();
        itemDto.setName("Щётка для обуви");
        itemDto.setDescription("Стандартная щётка для обуви");
        itemDto.setAvailable(true);
    }

    @Test
    void getAllItemsByIdUserWhenUserOwnerItemThenReturnedListItem() {
        Integer from = 0;
        Integer size = 5;
        List<Item> list = List.of(item);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        when(itemRepository.findByOwnerId(user.getId(), page)).thenReturn(new PageImpl<>(list));
        when(bookingRepository.findByItemIdIn(anySet(), any()))
                .thenReturn(List.of(booking));
        when(commentRepository.findByItemIdIn(anySet(), any())).thenReturn(List.of(comment));

        Collection<ItemInfo> actual = itemService.getAllItemsByIdUser(user.getId(), from, size);

        assertEquals(1, actual.size());
        verify(bookingRepository, times(1)).findByItemIdIn(anySet(), any());
        verify(commentRepository, times(1)).findByItemIdIn(anySet(), any());
        verify(itemRepository, times(1)).findByOwnerId(user.getId(), page);
    }

    @Test
    void getAllItemsByIdUserWhenUserNotOwnerItemThenReturnedEmptyList() {
        Integer from = 0;
        Integer size = 5;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        when(itemRepository.findByOwnerId(user.getId(), page)).thenReturn(Page.empty());

        Collection<ItemInfo> actual = itemService.getAllItemsByIdUser(user.getId(), from, size);

        assertTrue(actual.isEmpty());
        verify(itemRepository, times(1)).findByOwnerId(user.getId(), page);
    }

    @Test
    void getItemByIdWhenItemFoundAndUserOwnerThenReturnedItem() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedAsc(item.getId())).thenReturn(List.of(comment));

        ItemInfo actualItem = itemService.getItem(user.getId(), item.getId());

        assertEquals(item.getName(), actualItem.getName());
        assertEquals(item.getId(), actualItem.getId());

        verify(commentRepository, times(1)).findByItemIdOrderByCreatedAsc(item.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void getItemByIdWhenItemFoundAndUserNotOwnerThenReturnedItem() {
        Sort sort =  Sort.by(Sort.Direction.ASC, "start");
        BookingStatus status = BookingStatus.APPROVED;
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatus(item.getId(), status, sort)).thenReturn(List.of(booking));
        when(commentRepository.findByItemIdOrderByCreatedAsc(item.getId())).thenReturn(List.of(comment));

        ItemInfo actualItem = itemService.getItem(user.getId(), item.getId());

        assertEquals(item.getName(), actualItem.getName());
        assertEquals(item.getId(), actualItem.getId());
        verify(bookingRepository, times(1)).findByItemIdAndStatus(item.getId(), status, sort);
        verify(commentRepository, times(1)).findByItemIdOrderByCreatedAsc(item.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void getItemByIdWhenItemNotFoundThenThrowException() {
        long id = 0L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(user.getId(), id));
        verify(commentRepository, never()).findByItemIdOrderByCreatedAsc(id);
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void searchWhenItemFoundThenReturnedItemList() {
        long userId = 1L;
        String text = "щетка";
        Integer from = 0;
        Integer size = 5;
        List<Item> list = List.of(item);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.search(text, page)).thenReturn(new PageImpl<>(list));

        Collection<ItemDto> actual = itemService.search(userId, text, from, size);

        assertEquals(1, actual.size());
        verify(itemRepository, times(1)).search(text, page);
    }

    @Test
    void searchWhenItemNotFoundThenReturnedEmptyItemList() {
        long userId = 1L;
        String text = "щетка";
        Integer from = 0;
        Integer size = 5;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.search(text, page)).thenReturn(Page.empty());

        Collection<ItemDto> actual = itemService.search(userId, text, from, size);

        assertTrue(actual.isEmpty());
        verify(itemRepository, times(1)).search(text, page);
    }

    @Test
    void createWhenRequestNotEmptyThenReturnedItem() {
        item.setRequest(request);
        itemDto.setRequestId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actual = itemService.create(user.getId(), itemDto);

        assertEquals(item.getName(), actual.getName());
        assertEquals(item.getDescription(), actual.getDescription());
        assertEquals(item.getRequest().getId(), actual.getRequestId());
        verify(itemRepository, times(1)).save(any());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void createWhenRequestEmptyThenReturnedItem() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actual = itemService.create(user.getId(), itemDto);

        assertEquals(item.getName(), actual.getName());
        assertEquals(item.getDescription(), actual.getDescription());
        assertNull(actual.getRequestId());
        verify(itemRepository, times(1)).save(any());
        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void createWhenRequestEmptyNotFoundThenReturnedException() {
        itemDto.setRequestId(4L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(user.getId(), itemDto));
        verify(itemRepository, never()).save(any());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateWhenUpdateDescriptionNameAvailableThenReturnedItem() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findByIdAndOwnerId(item.getId(), user.getId())).thenReturn(Optional.ofNullable(item));
        itemDto.setDescription("updateDescription");
        itemDto.setName("update");
        itemDto.setAvailable(false);
        itemDto.setId(1L);
        when(itemRepository.save(any())).thenReturn(toNewItem(user, itemDto, null));

        ItemDto actual = itemService.update(user.getId(), item.getId(), itemDto);

        assertEquals(itemDto.getName(), actual.getName());
        assertEquals(itemDto.getDescription(), actual.getDescription());
        assertEquals(false, actual.getAvailable());
        verify(itemRepository, times(1)).findByIdAndOwnerId(item.getId(), user.getId());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateWhenDescriptionNameNullThenReturnedItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(item));
        itemDto.setDescription(null);
        itemDto.setName(null);
        itemDto.setAvailable(false);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actual = itemService.update(user.getId(), item.getId(), itemDto);

        assertEquals(item.getName(), actual.getName());
        assertEquals(item.getDescription(), actual.getDescription());
        assertEquals(false, actual.getAvailable());
        verify(itemRepository, times(1)).findByIdAndOwnerId(item.getId(), user.getId());
    }

    @Test
    void saveComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(comment.getText());
        commentDto.setCreated(LocalDateTime.of(2023, 5, 22, 1, 34, 1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(),
                any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDtoResponse commentActual = itemService.saveComment(user2.getId(), item.getId(), commentDto);

        assertEquals(commentDto.getText(), commentActual.getText());
        assertEquals(commentActual.getCreated(), commentDto.getCreated());
        verify(commentRepository, times(1)).save(any());

    }

    @Test
    void saveCommentWhenBookingEmptyThenReturnedException() {
        CommentDto commentDto = new CommentDto();
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(),
                any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.saveComment(user2.getId(), item.getId(), commentDto));
        verify(commentRepository, never()).save(any());
    }
}