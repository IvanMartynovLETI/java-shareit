package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.user.i.api.UserRepository;
import ru.practicum.shareit.user.i.impl.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    private User user1;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(1L, "user1", "user1@yandex.ru");
    }

    @Test
    public void saveUserWhenAllOKThenReturnUserTest() {
        when(userRepository.save(any()))
                .thenReturn(user1);

        User user = userService.saveUser(user1);

        assertThat(user).isEqualTo(user1);
    }

    @Test
    public void saveUserWhenDataIntegrityViolationThenThrowEntityAlreadyExistsExceptionTest() {
        when(userRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("User already exists"));

        assertThrows(EntityAlreadyExistsException.class, () -> userService.saveUser(user1));
    }

    @Test
    public void updateUserWhenAllOKThenReturnUserTest() {
        User user2 = new User(1L, "user2", "user2@yandex.ru");
        when(userRepository.findUserById(any()))
                .thenReturn(user2);

        when(userRepository.save(any()))
                .thenReturn(user1);

        User user = userService.updateUser(user1.getId(), user1);

        assertThat(user).isEqualTo(user1);
    }

    @Test
    public void updateUserWhenAttemptingToDuplicateEmailThenThrowEntityAlreadyExistsExceptionTest() {
        User user2 = new User(1L, "user2", "user2@yandex.ru");
        when(userRepository.findUserById(any()))
                .thenReturn(user2);

        when(userRepository.findUserByEmail(any()))
                .thenReturn(user2);

        assertThrows(EntityAlreadyExistsException.class, () -> userService.updateUser(1L, user1));
    }

    @Test
    public void getUserByIdWhenUserExistsThenReturnUserTest() {
        when(userRepository.findUserById(any()))
                .thenReturn(user1);

        assertThat(userService.getUserById(user1.getId())).isEqualTo(user1);
    }

    @Test
    public void getUserByIdWhenUserNotExistThenThrowEntityDoesNotExistExceptionTest() {
        when(userRepository.findUserById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> userService.getUserById(user1.getId()));
    }

    @Test
    public void deleteUserByIdWhenUserExistsThenReturnUser() {
        when(userRepository.findUserById(any()))
                .thenReturn(user1);

        assertThat(userService.deleteUserById(user1.getId())).isEqualTo(user1);
    }

    @Test
    public void deleteUserByIdWhenUserNotExistThenThrowEntityDoesNotExistExceptionTest() {
        when(userRepository.findUserById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> userService.deleteUserById(user1.getId()));
    }

    @Test
    public void getAllUsersThenReturnUsersTest() {
        List<User> users = new ArrayList<>();
        users.add(user1);

        when(userRepository.findAll())
                .thenReturn(users);

        assertThat(userService.getAllUsers().size()).isEqualTo(1);
        assertThat(userService.getAllUsers().get(0)).isEqualTo(user1);
    }
}