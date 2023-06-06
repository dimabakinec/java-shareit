package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addModel(UserDto userDto);

    UserDto updateModel(long id, UserDto userDto);

    UserDto findModelById(long id);

    List<UserDto> getAllModels();

    void deleteModelById(long id);
}