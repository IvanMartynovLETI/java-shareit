package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.i.api.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ItemRequestServiceIntegrationTests {
    private final ItemRequestService itemRequestService;

    private static User user1;
    private static ItemRequest itemRequest1;

    @BeforeAll
    public static void beforeAll() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description of item1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("description of request1");
        itemRequest1.setRequestor(user1);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        item1.setRequest(itemRequest1);
        itemRequest1.setItems(items);
    }

    @Test
    public void getItemRequestById_ThenReturnItemRequestTest() {
        ItemRequest itemRequest = itemRequestService.getItemRequestById(itemRequest1.getId());

        assertThat(itemRequest.getId()).isEqualTo(itemRequest1.getId());
        assertThat(itemRequest.getRequestor().getId()).isEqualTo(user1.getId());
    }

    @Test
    public void getAllItemRequestsByOwner_ThenReturnItemRequestsTest() {
        List<ItemRequest> itemRequests = itemRequestService.getAllRequestsByOwner(user1.getId());

        assertThat(itemRequests.size()).isEqualTo(1);
        assertThat(itemRequests.get(0).getId()).isEqualTo(itemRequest1.getId());
        assertThat(itemRequests.get(0).getRequestor().getId()).isEqualTo(user1.getId());
        assertThat(itemRequests.get(0).getDescription()).isEqualTo(itemRequest1.getDescription());
    }
}