package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto addModel(UserDto user) {
        return userStorage.add(user);
    }

    @Override
    public UserDto updateModel(long id, UserDto user) {
        return userStorage.update(id, user);
    }

    @Override
    public UserDto findModelById(long id) {
        return userStorage.findById(id);
    }

    @Override
    public List<UserDto> getAllModels() {
        return userStorage.getAll();
    }

    @Override
    public void deleteModelById(long id) {
        userStorage.deleteById(id);
    }
}