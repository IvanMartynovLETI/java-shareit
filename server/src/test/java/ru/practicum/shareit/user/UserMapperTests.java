package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserMapperTests {
    private final UserDtoMapper userDtoMapper;
    private static User user1;
    private static User user2;
    private static UserDto user1Dto;

    @BeforeAll
    public static void beforeAll() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@yandex.ru");

        user1Dto = new UserDto(1L, "user1", "user1@yandex.ru");
    }

    @Test
    public void userToUserDtoTest() {
        user1Dto = userDtoMapper.userToUserDto(user1);

        assertThat(user1Dto.getId()).isEqualTo(user1.getId());
        assertThat(user1Dto.getName()).isEqualTo(user1.getName());
        assertThat(user1Dto.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    public void userDtoToUserTest() {
        user1 = userDtoMapper.userDtoToUser(user1Dto);

        assertThat(user1.getId()).isEqualTo(user1Dto.getId());
        assertThat(user1.getName()).isEqualTo(user1Dto.getName());
        assertThat(user1.getEmail()).isEqualTo(user1Dto.getEmail());
    }

    @Test
    public void usersToDtosTest() {
        List<User> users = Arrays.asList(user1, user2);

        List<UserDto> dtos = userDtoMapper.usersToDtos(users);

        assertThat(dtos.size()).isEqualTo(2);
        assertThat(dtos.get(0).getId()).isEqualTo(users.get(0).getId());
        assertThat(dtos.get(0).getName()).isEqualTo(users.get(0).getName());
        assertThat(dtos.get(0).getEmail()).isEqualTo(users.get(0).getEmail());
        assertThat(dtos.get(1).getId()).isEqualTo(users.get(1).getId());
        assertThat(dtos.get(1).getName()).isEqualTo(users.get(1).getName());
        assertThat(dtos.get(1).getEmail()).isEqualTo(users.get(1).getEmail());
    }
}