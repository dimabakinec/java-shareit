package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {

    UserDto add(UserDto user);

    UserDto update(long id, UserDto user);

    UserDto findById(long id);

    List<UserDto> getAll();

    void deleteById(long id);
}