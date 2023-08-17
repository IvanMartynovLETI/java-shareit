package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserServiceIntegrationTests {
    private final UserService userService;
    private static User user1;
    private static User user2;
    private static User user3;

    @BeforeAll()
    public static void beforeAll() {
        user1 = new User(1L, "user1", "user1@yandex.ru");
        user2 = new User(2L, "user2", "user2@yandex.ru");
        user3 = new User(3L, "user3", "user3@yandex.ru");
    }

    @Test
    public void getAllUsersThenReturnUsersTest() {
        List<User> users = userService.getAllUsers();

        assertThat(users.size()).isEqualTo(3);
        assertThat(users).contains(user1);
        assertThat(users).contains(user2);
        assertThat(users).contains(user3);
    }

    @Test
    public void createUserThenReturnUserTest() {
        User user4 = new User(4L, "user4", "user4@yandex.ru");
        User userReturned = userService.saveUser(user4);

        assertThat(userReturned).isEqualTo(user4);
    }
}