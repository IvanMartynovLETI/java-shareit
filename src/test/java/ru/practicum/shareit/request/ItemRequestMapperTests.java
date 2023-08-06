package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestMapperTests {
    private final ItemRequestDtoMapper itemRequestDtoMapper;
    private static User user1;
    private static ItemRequest itemRequest1;
    private static ItemRequestDto itemRequestDto1;

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
        LocalDateTime ndtm = LocalDateTime.now();
        itemRequest1.setCreated(ndtm);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        item1.setRequest(itemRequest1);
        itemRequest1.setItems(items);
    }

    @Test
    public void itemRequestToDtoTest() {
        itemRequestDto1 = itemRequestDtoMapper.itemRequestToDto(itemRequest1);

        assertThat(itemRequestDto1.getId()).isEqualTo(itemRequest1.getId());
        assertThat(itemRequestDto1.getDescription()).isEqualTo(itemRequest1.getDescription());
        assertThat(itemRequestDto1.getUserId()).isEqualTo(itemRequest1.getRequestor().getId());
        assertThat(itemRequestDto1.getCreated()).isEqualTo(itemRequest1.getCreated());
        assertThat(itemRequestDto1.getItems().size()).isEqualTo(1);
        assertThat(itemRequestDto1.getItems().get(0).getId()).isEqualTo(itemRequest1.getItems().get(0).getId());
        assertThat(itemRequestDto1.getItems().get(0).getName()).isEqualTo(itemRequest1.getItems().get(0).getName());
        assertThat(itemRequestDto1.getItems().get(0).getDescription()).isEqualTo(itemRequest1.getItems().get(0)
                .getDescription());
        assertThat(itemRequestDto1.getItems().get(0).getAvailable()).isEqualTo(itemRequest1.getItems().get(0)
                .getAvailable());
        assertThat(itemRequestDto1.getItems().get(0).getRequestId()).isEqualTo(itemRequest1.getId());
    }

    @Test
    public void itemRequestDtoToItemRequestTest() {
        itemRequestDto1 = itemRequestDtoMapper.itemRequestToDto(itemRequest1);
        UserService userService = Mockito.mock(UserService.class);
        Mockito
                .when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(user1);

        ReflectionTestUtils.setField(itemRequestDtoMapper, "userService", userService);

        itemRequest1 = itemRequestDtoMapper.itemRequestDtoToItemRequest(user1.getId(), itemRequestDto1);

        assertThat(itemRequest1.getId()).isEqualTo(itemRequestDto1.getId());
        assertThat(itemRequest1.getDescription()).isEqualTo(itemRequestDto1.getDescription());
        assertThat(itemRequest1.getRequestor()).isEqualTo(user1);
        assertThat(itemRequest1.getItems().size()).isEqualTo(1);
        assertThat(itemRequest1.getItems().get(0).getId()).isEqualTo(itemRequestDto1.getItems().get(0).getId());
        assertThat(itemRequest1.getItems().get(0).getName()).isEqualTo(itemRequestDto1.getItems().get(0).getName());
        assertThat(itemRequest1.getItems().get(0).getDescription()).isEqualTo(itemRequestDto1.getItems().get(0)
                .getDescription());
        assertThat(itemRequest1.getItems().get(0).getAvailable()).isEqualTo(itemRequestDto1.getItems().get(0)
                .getAvailable());
        assertThat(itemRequest1.getId()).isEqualTo(itemRequestDto1.getItems().get(0).getRequestId());
    }

    @Test
    public void itemRequestsToDtosTest() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest1);

        List<ItemRequestDto> itemRequestDtos = itemRequestDtoMapper.itemRequestsToDtos(itemRequests);

        assertThat(itemRequestDtos.size()).isEqualTo(itemRequests.size());
        assertThat(itemRequestDtos.get(0).getId()).isEqualTo(itemRequests.get(0).getId());
        assertThat(itemRequestDtos.get(0).getDescription()).isEqualTo(itemRequests.get(0).getDescription());
        assertThat(itemRequestDtos.get(0).getUserId()).isEqualTo(itemRequests.get(0).getRequestor().getId());
        assertThat(itemRequestDtos.get(0).getCreated()).isEqualTo(itemRequests.get(0).getCreated());
        assertThat(itemRequestDtos.get(0).getItems().get(0).getId()).isEqualTo(itemRequests.get(0).getItems().get(0)
                .getId());
        assertThat(itemRequestDtos.get(0).getItems().get(0).getName()).isEqualTo(itemRequests.get(0).getItems().get(0)
                .getName());
        assertThat(itemRequestDtos.get(0).getItems().get(0).getDescription()).isEqualTo(itemRequests.get(0).getItems()
                .get(0).getDescription());
        assertThat(itemRequestDtos.get(0).getItems().get(0).getAvailable()).isEqualTo(itemRequests.get(0).getItems()
                .get(0).getAvailable());
        assertThat(itemRequestDtos.get(0).getItems().get(0).getRequestId()).isEqualTo(itemRequests.get(0).getId());
    }
}