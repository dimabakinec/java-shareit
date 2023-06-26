package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = { "db.name=test"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final UserService userService;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("user");
        userDto.setEmail("user@user.com");
    }

    @Test
    void saveModel() {
        UserDto result = userService.addModel(userDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateModel() {
        long userId = userService.addModel(userDto).getId();
        UserDto userUp = new UserDto(userId, "userUp", "userUp@user.com");

        UserDto result = userService.updateModel(userId, userUp);

        assertEquals(userUp.getId(), result.getId());
        assertThat(userUp.getName(), equalTo(result.getName()));
        assertThat(userUp.getEmail(), equalTo(result.getEmail()));
    }

    @Test
    void updateModelWhenInvalidUserIdThenReturnedException() {
        long userId = userService.addModel(userDto).getId();
        UserDto userUp = new UserDto(userId, "userUp", "userUp@user.com");

        assertThrows(NotFoundException.class, () -> userService.updateModel(2L, userUp));
    }

    @Test
    void updateModelWhenNullNameAndEmailThenReturnedException() {
        long userId = userService.addModel(userDto).getId();
        UserDto userUp = new UserDto(userId, null, null);

        UserDto result = userService.updateModel(userId, userUp);

        assertEquals(userUp.getId(), result.getId());
        assertThat(userDto.getName(), equalTo(result.getName()));
        assertThat(userDto.getEmail(), equalTo(result.getEmail()));
    }
}