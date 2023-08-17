package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("ShareIt gateway: request for user creation obtained.");

        return userClient.saveUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable final Long id, @RequestBody UserDto userDto) {
        log.info("ShareIt gateway: request for user with id: '{}' update obtained.", id);

        return userClient.updateUser(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable final long id) {
        log.info("ShareIt gateway: request for getting user by id: '{}' obtained.", id);

        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable final Long id) {
        log.info("ShareIt gateway: request for deleting user by id: '{}' obtained.", id);

        userClient.deleteUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("ShareIt gateway: request for getting all users obtained.");

        return userClient.getAllUsers();
    }
}