package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private PageRequest page;
    private User userResult;
    private User userResult2;
    private Item itemResult;
    private Item itemResult2;
    private ItemRequest requestResult;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.com");
        userResult = userRepository.save(user);
        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user.com");
        userResult2 = userRepository.save(user2);

        Item item = new Item();
        item.setName("Щётка для обуви");
        item.setDescription("Стандартная щётка для обуви");
        item.setAvailable(false);
        item.setOwner(userResult);
        itemResult = repository.save(item);

        ItemRequest request = new ItemRequest();
        request.setDescription("Description");
        request.setRequester(userResult2);
        request.setCreated(LocalDateTime.now());
        requestResult = requestRepository.save(request);

        Item item2 = new Item();
        item2.setName("Краска для обуви");
        item2.setDescription("Стандартная краска для обуви");
        item2.setAvailable(true);
        item2.setOwner(userResult);
        item2.setRequest(requestResult);
        itemResult2 = repository.save(item2);

        int from = 0;
        int size = 10;
        page = PageRequest.of(from > 0 ? from / size : 0, size);
    }

    @Test
    void searchByTextThenReturnedItem() {
        String text = "краска";

        List<Item> items = repository.search(text, page).toList();

        assertEquals(1, items.size(), "Размер списка не равен 1");
        assertEquals("Краска для обуви", items.get(0).getName(), "Значения не равны");
    }

    @Test
    void searchByTextThenReturnedEmptyList() {
        String text = "дрель";

        List<Item> items = repository.search(text, page).toList();

        assertTrue(items.isEmpty());
    }

    @Test
    void searchByTextWhenItemNotAvailableThenReturnedEmptyList() {
        String text = "щетка";

        List<Item> items = repository.search(text, page).toList();

        assertTrue(items.isEmpty());
    }

    @Test
    void findByOwnerIdWhenItemOrUserNotFoundThenReturnedEmpty() {
        long userId = 0L;
        Page<Item> items = repository.findByOwnerId(userId, page);

        assertTrue(items.isEmpty());
    }

    @Test
    void findByOwnerIdWhenItemFoundThenReturnedItemList() {
        List<Item> items = repository.findByOwnerId(userResult.getId(), page).getContent();

        assertEquals(2, items.size());
    }

    @Test
    void findByIdAndOwnerIdWhenItemFoundThenReturnedItem() {
        Optional<Item>  item = repository.findByIdAndOwnerId(itemResult.getId(), userResult.getId());

        assertTrue(item.isPresent());
    }

    @Test
    void findByIdAndOwnerIdWhenItemNotFoundThenReturnedEmpty() {
        long itemId = 0L;
        Optional<Item>  item = repository.findByIdAndOwnerId(itemId, userResult.getId());

        assertTrue(item.isEmpty());
    }

    @Test
    void findByRequestIdInWhenRequestNotFoundThenReturnedEmptyList() {
        List<Item> items = repository.findByRequestIdIn(new HashSet<>());

        assertTrue(items.isEmpty());
    }

    @Test
    void findByRequestIdInWhenRequestFoundThenReturnedItemList() {
        Set<Long> requestId = new HashSet<>();
        requestId.add(requestResult.getId());
        List<Item> items = repository.findByRequestIdIn(requestId);

        assertEquals(1, items.size());
        assertEquals(itemResult2.getId(), items.get(0).getId());
    }

    @Test
    void findByRequestIdWhenRequestFoundThenReturnedItemList() {
        List<Item> items = repository.findByRequestId(requestResult.getId());

        assertEquals(1, items.size());
        assertEquals(itemResult2.getId(), items.get(0).getId());
    }

    @Test
    void findByRequestIdWhenItemNotFoundThenReturnedItemList() {
        long requestId = 2L;
        List<Item> items = repository.findByRequestId(requestId);

        assertTrue(items.isEmpty());
    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
        userRepository.deleteAll();
    }
}