package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    private final Long userId = 1L;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final LocalDateTime created = LocalDateTime.now();
    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "Хотел бы воспользоваться щёткой для обуви",
            created
    );

    private final ItemDto itemDto = new ItemDto(
            5L,
            "Щётка для обуви",
            "Стандартная щётка для обуви",
            true,
            1L
    );

    private final Set<ItemDto> itemDtoSet = new HashSet<>();

    private final ItemRequestInfo requestInfo = new ItemRequestInfo(
            1L,
            "Хотел бы воспользоваться щёткой для обуви",
            LocalDateTime.now(),
            itemDtoSet
    );

    @Test
    void saveItemRequestThenStatus200andItemRequestReturned() throws Exception {
        when(requestService.addNewRequestItem(anyLong(), any())).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().format(TIME_FORMATTER))));
    }

    @Test
    void getItemRequestByIdRequester() throws Exception {
        when(requestService.getAllRequestItemByRequesterId(anyLong())).thenReturn(List.of(requestInfo));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestInfo.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDto.getCreated()
                        .format(TIME_FORMATTER))))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @Test
    void getAllItemRequestByUserIdNotRequesterThenStatus200andItemRequestReturned() throws Exception {
        Long userIdNew = 5L;
        Integer from = 0;
        Integer size = 5;

        when(requestService.getAllRequestItemByUserId(userIdNew,from, size)).thenReturn(List.of(requestInfo));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userIdNew)
                        .accept(MediaType.ALL)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestInfo.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDto.getCreated()
                        .format(TIME_FORMATTER))))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @Test
    void getAllItemRequestThenStatus200andItemRequestReturned() throws Exception {
        Integer from = 0;
        Integer size = 5;

        when(requestService.getAllRequestItemByUserId(userId,from, size)).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.ALL)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getItemRequestByIdThenStatus200andItemRequestReturned() throws Exception {
        itemDtoSet.add(itemDto);
        when(requestService.getItemRequestById(anyLong(), anyLong())).thenReturn(requestInfo);

        mvc.perform(get("/requests/{requestId}", requestInfo.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestInfo.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestInfo.getDescription())))
                .andExpect(jsonPath("$.created", is(requestInfo.getCreated()
                        .format(TIME_FORMATTER))))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemDto.getRequestId()), Long.class));
    }
}