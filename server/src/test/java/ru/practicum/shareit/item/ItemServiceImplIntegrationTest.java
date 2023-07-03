package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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
class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private User userResult;
    private Item itemResult;
    private Booking booking;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.com");
        userResult = userRepository.save(user);

        Item item = new Item();
        item.setName("Щётка для обуви");
        item.setDescription("Стандартная щётка для обуви");
        item.setAvailable(true);
        item.setOwner(userResult);
        itemResult = itemRepository.save(item);

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user.com");
        User resultUser2 = userRepository.save(user2);

        booking = new Booking();
        booking.setStart(LocalDateTime.of(2023, 5, 23, 1, 34, 1));
        booking.setEnd(LocalDateTime.of(2023, 5, 24, 0, 34, 1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(itemResult);
        booking.setBooker(resultUser2);
        bookingRepository.save(booking);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment");
        comment.setAuthor(user2);
        comment.setItem(itemResult);
        comment.setCreated(LocalDateTime.of(2023, 5, 24, 1, 34, 1));
        commentRepository.save(comment);
    }

    @Test
    void getAllItemsByIdUserWhenItemNotFoundThenReturnedEmptyList() {
        long userId = 0L;
        int from = 0;
        int size = 5;

        Collection<ItemInfo> result = itemService.getAllItemsByIdUser(userId, from, size);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllItemsByIdUserWhenItemFoundThenReturnedEmptyList() {
        long userId = userResult.getId();
        int from = 0;
        int size = 5;

        List<ItemInfo> result = new ArrayList<>(itemService.getAllItemsByIdUser(userId, from, size));

        assertEquals(1, result.size());
        assertEquals(itemResult.getName(), result.get(0).getName());
        assertEquals(booking.getStart(), result.get(0).getLastBooking().getStart());
        assertEquals(1, result.get(0).getComments().size());
    }
}