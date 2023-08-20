package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.i.api.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @PostMapping
    public UserDto saveNewUser(@RequestBody UserDto userDto) {
        log.info("Controller layer: request for user creation obtained.");

        return userDtoMapper.userToUserDto(userService.saveUser(userDtoMapper.userDtoToUser(userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable final Long id, @RequestBody UserDto userDto) {
        log.info("Controller layer: request for user with id: '{}' update obtained.", id);

        return userDtoMapper.userToUserDto(userService.updateUser(id, userDtoMapper.userDtoToUser(userDto)));
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable final long id) {
        log.info("Controller layer: request for getting user by id: '{}' obtained.", id);

        return userDtoMapper.userToUserDto(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUserById(@PathVariable final Long id) {
        log.info("Controller layer: request for deleting user by id: '{}' obtained.", id);

        return userDtoMapper.userToUserDto(userService.deleteUserById(id));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Controller layer: request for getting all users obtained.");

        return userDtoMapper.usersToDtos(userService.getAllUsers());
    }
}