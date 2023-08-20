package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.i.api.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ItemServiceIntegrationTests {
    private final ItemService itemService;
    private static User user1;
    private static Item item1;

    @BeforeAll
    public static void beforeAll() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description of item1");
        item1.setAvailable(true);
        item1.setOwner(user1);
    }

    @Test
    public void getAllItemsOfOwnerThenReturnItemsTest() {
        List<Item> items = itemService.getAllItemsOfOwner(user1.getId(), 0, 25);

        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0)).isEqualTo(item1);
    }

    @Test
    public void createItemThenReturnItemTest() {
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("item2");
        item2.setDescription("description of item2");
        item2.setAvailable(true);
        item2.setOwner(user1);

        assertThat(itemService.saveItem(user1.getId(), item2)).isEqualTo(item2);
    }
}