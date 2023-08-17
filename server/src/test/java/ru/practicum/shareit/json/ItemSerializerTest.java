package ru.practicum.shareit.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.serializer.ItemSerializer;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.*;
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemSerializerTest {
    private final ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Item.class, new ItemSerializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void itemSerializerTest() throws JsonProcessingException {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description of item1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1.setRequest(itemRequest1);

        String str = "{\"id\":1,\"name\":\"item1\",\"description\":\"description of item1\",\"available\":true," +
                "\"requestId\":1}";
        String json = objectMapper.writeValueAsString(item1);

        assertThat(json).isEqualTo(str);
    }
}