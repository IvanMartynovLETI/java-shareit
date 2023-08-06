package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {
    private UserDto userDto1;
    private User user1;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @MockBean
    UserDtoMapper userDtoMapper;

    @Autowired
    MockMvc mvc;

    @BeforeEach
    public void beforeEach() {
        userDto1 = new UserDto(1L, "user1", "user1@rambler.ru");
        user1 = new User(1L, "user1", "user1@rambler.ru");
    }

    @SneakyThrows
    @Test
    void createUser_WhenNoErrors_ThenReturnSavedUserTest() {
        when(userDtoMapper.userDtoToUser(any()))
                .thenReturn(user1);

        when(userDtoMapper.userToUserDto(any()))
                .thenReturn(userDto1);

        when(userService.saveUser(any()))
                .thenReturn(user1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @SneakyThrows
    @Test
    void createUser_WhenUserExists_ThenReturn409ErrorCodeTest() {
        when(userDtoMapper.userDtoToUser(any()))
                .thenReturn(user1);

        when(userDtoMapper.userToUserDto(any()))
                .thenReturn(userDto1);

        when(userService.saveUser(any()))
                .thenThrow(new EntityAlreadyExistsException("User is already exists in database"));

        mvc.perform(post("/users"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateUser_WhenNoErrors_ThenReturnUpdatedUseTest() {
        UserDto userDto1Updated = new UserDto(1L, "user1Updated", "user1@yandex.ru");

        when(userDtoMapper.userDtoToUser(any()))
                .thenReturn(user1);

        when(userService.updateUser(any(), any()))
                .thenReturn(user1);

        when(userDtoMapper.userToUserDto(any()))
                .thenReturn(userDto1Updated);

        mvc.perform(patch("/users/{userId}", userDto1Updated.getId())
                        .content(mapper.writeValueAsString(userDto1Updated))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1Updated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1Updated.getName())))
                .andExpect(jsonPath("$.email", is(userDto1Updated.getEmail())));
    }

    @SneakyThrows
    @Test
    public void updateUser_WhenEmailNotUnique_ThenReturn409ErrorCodeTest() {
        when(userDtoMapper.userDtoToUser(any()))
                .thenReturn(user1);

        when(userService.updateUser(any(), any()))
                .thenReturn(user1);

        when(userDtoMapper.userToUserDto(any()))
                .thenThrow(new EntityAlreadyExistsException("User is already exists in database"));

        mvc.perform(patch("/users/{userId}", userDto1.getId()))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getUserById_WhenUserExists_ThenReturnUserTest() {
        when(userDtoMapper.userDtoToUser(any()))
                .thenReturn(user1);

        when(userDtoMapper.userToUserDto(any()))
                .thenReturn(userDto1);

        when(userService.getUserById(any()))
                .thenReturn(user1);

        mvc.perform(get("/users/{id}", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @SneakyThrows
    @Test
    void getUserById_WhenUserDoesNotExist_ThenReturn404ErrorCodeTest() {
        when(userDtoMapper.userDtoToUser(any()))
                .thenReturn(user1);

        when(userDtoMapper.userToUserDto(any()))
                .thenThrow(new EntityDoesNotExistException("User doesn't exist in database"));

        when(userService.getUserById(any()))
                .thenReturn(user1);

        mvc.perform(get("/users/{id}", userDto1.getId()))
                .andExpect(status().isNotFound());

    }

    @SneakyThrows
    @Test
    public void deleteUser_WhenNoErrors_ThenReturnDeletedUserTest() {
        when(userDtoMapper.userDtoToUser(any()))
                .thenReturn(user1);

        when(userService.updateUser(any(), any()))
                .thenReturn(user1);

        when(userDtoMapper.userToUserDto(any()))
                .thenReturn(userDto1);

        mvc.perform(delete("/users/{id}", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @SneakyThrows
    @Test
    public void deleteUser_WhenUserDoesNotExist_ThenReturn404ErrorCodeTest() {
        when(userDtoMapper.userDtoToUser(any()))
                .thenReturn(user1);

        when(userService.deleteUserById(any()))
                .thenReturn(user1);

        when(userDtoMapper.userToUserDto(any()))
                .thenThrow(new EntityDoesNotExistException("User doesn't exist in database"));

        mvc.perform(delete("/users/{id}", userDto1.getId()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void getAllUsers_WhenUsersExist_ThenReturnUsersTest() {
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(userDto1);

        when(userDtoMapper.userDtoToUser(any()))
                .thenReturn(user1);

        when(userService.getAllUsers())
                .thenReturn(Collections.emptyList());

        when(userDtoMapper.usersToDtos(any()))
                .thenReturn(userDtoList);

        String response = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(mapper.writeValueAsString(userDtoList)).isEqualTo(response);
    }
}