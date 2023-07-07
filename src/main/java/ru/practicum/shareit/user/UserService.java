package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    User updateUser(Long id, User user);

    User getUserById(Long id);

    User deleteUserById(Long id);

    List<User> getAllUsers();
}