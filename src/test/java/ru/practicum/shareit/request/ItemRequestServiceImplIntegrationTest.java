package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = { "db.name=test"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestService service;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User userResult;
    private User resultUser2;
    private User userResult3;
    private ItemRequest resultRequest;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.com");
        userResult = userRepository.save(user);

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user.com");
        resultUser2 = userRepository.save(user2);

        ItemRequest request = new ItemRequest();
        request.setDescription("Щётка для обуви");
        request.setCreated(LocalDateTime.of(2023, 5, 23, 1, 34, 1));
        request.setRequester(resultUser2);
        resultRequest = requestRepository.save(request);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Краска для обуви");
        request2.setCreated(LocalDateTime.of(2023, 5, 23, 1, 34, 1));
        request2.setRequester(userResult);
        requestRepository.save(request2);

        Item item = new Item();
        item.setName("Щётка для обуви");
        item.setDescription("Стандартная щётка для обуви");
        item.setAvailable(true);
        item.setOwner(userResult);
        item.setRequest(resultRequest);
        itemRepository.save(item);

        User user3 = new User();
        user3.setName("user3");
        user3.setEmail("user3@user.com");
        userResult3 = userRepository.save(user3);
    }

    @Test
    void getAllRequestItemByRequesterIdWhenUserNotFoundThenReturnedException() {
        long userId = 0L;

        assertThrows(NotFoundException.class, () -> service.getAllRequestItemByRequesterId(userId));
    }

    @Test
    void getAllRequestItemByRequesterIdWhenRequestNotFoundThenReturnedEmptyList() {
        Collection<ItemRequestInfo> result = service.getAllRequestItemByRequesterId(userResult3.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequestItemByRequesterIdWhenRequestAndItemFoundThenReturnedEmptyList() {
        List<ItemRequestInfo> result = new ArrayList<>(service.getAllRequestItemByRequesterId(resultUser2.getId()));

        assertEquals(1, result.size());
        assertEquals(resultRequest.getDescription(), result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getAllRequestItemByUserId() {
        int from = 0;
        int size = 5;
        Collection<ItemRequestInfo> listRequest = service.getAllRequestItemByUserId(userResult3.getId(), from, size);

        assertEquals(2, listRequest.size());

        List<ItemRequestInfo> listRequest2 = new ArrayList<>(service.getAllRequestItemByUserId(userResult.getId(),
                from, size));

        assertEquals(1, listRequest2.size());
        assertEquals(resultRequest.getDescription(), listRequest2.get(0).getDescription());
    }
}