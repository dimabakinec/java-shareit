package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private UserDto userDto;
    private User result;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("user");
        userDto.setEmail("user@user.com");

        result = new User();
        result.setId(1L);
        result.setName("userUpdate");
        result.setEmail("userUpdate@user.com");
    }

    @Test
    void addModelThenReturnedUser() {
        when(userRepository.save(toUser(userDto))).thenReturn(toUser(userDto));

        UserDto actualUser = userService.addModel(userDto);

        assertEquals(userDto, actualUser);
        verify(userRepository, times(1)).save(toUser(userDto));
    }

    @Test
    void updateModelWhenUserFoundThenReturnedUser() {
        long userId = 1L;
        UserDto result = new UserDto(
                1L,
                "userUpdate",
                "userUpdate@user.com"
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(result)));

        UserDto actualUser = userService.updateModel(userId, userDto);

        assertEquals(userDto, actualUser);

        verify(userRepository, times(1)).save(toUser(userDto));
    }

    @Test
    void shouldNotUpdateUserWhenUserFoundNameAndEmailNullThenReturnedUser() {
        long userId = 1L;
        userDto.setName(null);
        userDto.setEmail(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(result));

        UserDto actualUser = userService.updateModel(userId, userDto);

        assertEquals(result.getName(), actualUser.getName());
        assertEquals(result.getEmail(), actualUser.getEmail());

        verify(userRepository, times(1)).save(result);
    }

    @Test
    void findModelByIdWhenUserFoundThenReturnedUser() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));

        UserDto actualUser = userService.findModelById(userId);

        assertEquals(userDto, actualUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findModelByIdWhenUserNotFoundThenThrow() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findModelById(userId));
    }

    @Test
    void getAllModels() {
        when(userRepository.findAll()).thenReturn(List.of(result));

        List<UserDto> actualUserList = userService.getAllModels();

        assertEquals(1, actualUserList.size());
        assertEquals(1, actualUserList.get(0).getId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllModelsWhenUserNotFoundThenEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> actualUserList = userService.getAllModels();

        assertTrue(actualUserList.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteModelById() {
        long id = 1;
        willDoNothing().given(userRepository).deleteById(id);

        userService.deleteModelById(id);

        verify(userRepository, times(1)).deleteById(id);
    }
}