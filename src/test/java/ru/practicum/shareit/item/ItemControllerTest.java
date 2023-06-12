package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS");
    private final Long userId = 1L;
    private final ItemDto itemDto = new ItemDto(
            1L,
            "Щётка для обуви",
            "Стандартная щётка для обуви",
            true,
            userId
    );
    private final BookingDtoForItem lastBooking = new BookingDtoForItem(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            2L
    );
    private final BookingDtoForItem nextBooking = new BookingDtoForItem(
            1L,
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(3),
            2L
    );
    private final Set<CommentDtoResponse> comments = new HashSet<>();
    private final ItemInfo itemInfo = new ItemInfo(
            1L,
            "Щётка для обуви",
            "Стандартная щётка для обуви",
            true,
            lastBooking,
            nextBooking,
            comments
    );

    @Test
    void getAllItemsByIdUserIsEmptyListReturned() throws Exception {
        Long userIdNew = 5L;
        Integer from = 0;
        Integer size = 5;
        when(itemService.getAllItemsByIdUser(userIdNew, from, size)).thenReturn(Collections.emptyList());

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userIdNew)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void searchByTextThenStatus200andListItemReturned() throws Exception {
        Long userIdNew = 5L;
        Integer from = 0;
        Integer size = 5;
        String text = "щётка";

        when(itemService.search(userIdNew, text, from, size)).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userIdNew)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void saveItemThenStatus200andItemReturned() throws Exception {
        when(itemService.create(anyLong(), any())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getItemByIDThenStatus200andItemReturned() throws Exception {
        when(itemService.getItem(anyLong(),anyLong())).thenReturn(itemInfo);

        mvc.perform(get("/items/{itemId}", itemInfo.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemInfo.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemInfo.getName())))
                .andExpect(jsonPath("$.description", is(itemInfo.getDescription())))
                .andExpect(jsonPath("$.available", is(itemInfo.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemInfo.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemInfo.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemInfo.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemInfo.getNextBooking().getBookerId()), Long.class))
        ;
    }

    @Test
    void updateItemThenStatus200andItemReturned() throws Exception {
        itemDto.setName("Щётка для обуви металлическая");
        itemDto.setDescription("Стандартная щётка для обуви металлическая");
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void addCommentThenStatus200andCommentReturned() throws Exception {
        Long userIdNew = 5L;
        CommentDtoResponse comment = new CommentDtoResponse(
                1L,
                "Comment for item 1",
                "user",
                LocalDateTime.now()
        );

        when(itemService.saveComment(anyLong(),anyLong(), any())).thenReturn(comment);

        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userIdNew)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.created", is(comment.getCreated().format(TIME_FORMATTER))));
    }
}