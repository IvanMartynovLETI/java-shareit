package ru.practicum.shareit.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.serializer.UserSerializer;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.*;
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserSerializerTest {
    private final ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(User.class, new UserSerializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void itemSerializerTest() throws JsonProcessingException {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        String str = "{\"id\":1,\"name\":\"user1\",\"email\":\"user1@yandex.ru\"}";
        String json = objectMapper.writeValueAsString(user1);

        assertThat(json).isEqualTo(str);
    }
}