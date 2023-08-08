package ru.practicum.shareit.user.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.user.i.api.UserRepository;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User saveUser(User user) {
        log.info("Service layer: create user with email: '{}'.", user.getEmail());

        User savedUser;

        try {
            savedUser = repository.save(user);
        } catch (DataIntegrityViolationException e) {
            String userWarning = "User with email: " + user.getEmail() + " is already exists in database.";
            throw new EntityAlreadyExistsException(userWarning);
        }

        return savedUser;
    }

    @Transactional
    @Override
    public User updateUser(Long id, User user) {
        log.info("Service layer: update user with id: '{}'.", id);

        User userFromDataBase = repository.findUserById(id);

        if (user.getName() != null) {
            userFromDataBase.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().equals(userFromDataBase.getEmail())) {
            if (repository.findUserByEmail(user.getEmail()) != null) {
                throw new EntityAlreadyExistsException("Attempt to create a duplicate email: " + user.getEmail());
            }
            userFromDataBase.setEmail(user.getEmail());
        }

        return repository.save(userFromDataBase);
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        log.info("Service layer: get user by id: '{}'.", id);

        User userFound = repository.findUserById(id);

        if (userFound == null) {
            String userWarning = "User with id: " + id + " doesn't exist in database.";
            throw new EntityDoesNotExistException(userWarning);
        }

        return userFound;
    }

    @Transactional
    @Override
    public User deleteUserById(Long id) {
        log.info("Service layer: delete user by id: '{}'.", id);

        User userFound = repository.findUserById(id);

        if (userFound == null) {
            String userWarning = "User with id: " + id + " doesn't exist in database.";
            throw new EntityDoesNotExistException(userWarning);
        }

        repository.deleteById(id);

        return userFound;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Service layer: get all users.");

        return repository.findAll();
    }
}