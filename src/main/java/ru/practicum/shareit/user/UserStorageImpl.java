package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationExceptionOnDuplicate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;
import static ru.practicum.shareit.utils.Message.*;

@Component
@Slf4j
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long id = 0L;

    private void validationContain(long id) {
        if (!users.containsKey(id)) {
            log.error(MODEL_NOT_FOUND.getMessage() + id);
            throw new NotFoundException(MODEL_NOT_FOUND.getMessage() + id);
        }
    }

    private void validationDuplicate(UserDto userDto) {
        if (emails.contains(userDto.getEmail())) {
            log.error(DUPLICATE.getMessage());
            throw new ValidationExceptionOnDuplicate(DUPLICATE.getMessage());
        }
    }

    @Override
    public UserDto add(UserDto userDto) {
        validationDuplicate(userDto);
        id++;
        User user = UserMapper.toUser(id, userDto);
        users.put(id, user);
        emails.add(userDto.getEmail());
        log.info(ADD_MODEL.getMessage(), user);
        return toUserDto(user);
    }

    @Override
    public UserDto update(long id, UserDto user) {
        validationContain(id);
        User userUpdate = users.get(id);
        if (user.getEmail() != null && !userUpdate.getEmail().equals(user.getEmail())) {
            validationDuplicate(user);
            emails.remove(userUpdate.getEmail());
            userUpdate.setEmail(user.getEmail());
            emails.add(user.getEmail());
        }
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        users.put(id, userUpdate);
        return toUserDto(userUpdate);
    }

    @Override
    public UserDto findById(long id) {
        validationContain(id);
        return toUserDto(users.get(id));
    }

    @Override
    public List<UserDto> getAll() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(long id) {
        validationContain(id);
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }
}