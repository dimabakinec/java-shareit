package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;
import static ru.practicum.shareit.utils.Message.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto addModel(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info(ADD_MODEL.getMessage(), user);
        return toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateModel(long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + id));
        if (userDto.getEmail() != null && !user.getEmail().equals(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto findModelById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MODEL_NOT_FOUND.getMessage() + id));
        return toUserDto(user);
    }

    @Override
    public List<UserDto> getAllModels() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteModelById(long id) {
        userRepository.deleteById(id);
    }
}