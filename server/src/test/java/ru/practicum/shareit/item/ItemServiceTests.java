package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.i.api.ItemRepository;
import ru.practicum.shareit.item.i.impl.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {
    private static User user1;
    private static Item item1;
    private static ItemRequest itemRequest1;

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserService userService;

    @BeforeAll
    public static void beforeAll() {
        LocalDateTime ndtm = LocalDateTime.now();

        user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@yandex.ru");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description of item1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        item1.setRequest(itemRequest1);

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(ndtm.minusMinutes(10L));
        lastBooking.setEnd(ndtm.plusHours(1L));
        lastBooking.setItem(item1);
        lastBooking.setBooker(user2);
        lastBooking.setStatus(Status.APPROVED);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStart(ndtm.plusMinutes(10L));
        nextBooking.setEnd(ndtm.plusHours(1L));
        nextBooking.setItem(item1);
        nextBooking.setBooker(user2);
        nextBooking.setStatus(Status.APPROVED);
    }

    @Test
    public void saveItemWhenOwnerExistsThenReturnItemTest() {
        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(itemRepository.save(any()))
                .thenReturn(item1);

        assertThat(itemService.saveItem(user1.getId(), item1)).isEqualTo(item1);
    }

    @Test
    public void saveItemWhenOwnerNotExistThenThrowEntityDoesNotExistExceptionTest() {
        when(userService.getUserById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> itemService.saveItem(user1.getId(), item1));
    }

    @Test
    public void updateItemWhenAllOKThenReturnItemTest() {
        when(itemRepository.findItemById(any()))
                .thenReturn(item1);

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(itemRepository.save(any()))
                .thenReturn(item1);

        assertThat(itemService.updateItem(user1.getId(), item1.getId(), item1)).isEqualTo(item1);
    }

    @Test
    public void updateItemWhenOwnerNotExistThenThrowEntityDoesNotExistExceptionTest() {
        when(userService.getUserById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () ->
                itemService.updateItem(user1.getId(), item1.getId(), item1));
    }

    @Test
    public void getItemByIdWhenItemExistsThenReturnItemTest() {
        when(itemRepository.findItemById(any()))
                .thenReturn(item1);

        assertThat(itemService.getItemById(item1.getId())).isEqualTo(item1);
    }

    @Test
    public void getItemByIdWhenItemNotExistThenThrowEntityDoesNotExistExceptionTest() {
        when(itemRepository.findItemById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () ->
                itemService.getItemById(user1.getId()));
    }

    @Test
    public void getAllItemsOfOwnerWhenParamsOKThenReturnItemsTest() {
        List<Item> items = new ArrayList<>();
        items.add(item1);

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(itemRepository.findAllItemsByOwnerOrderByIdAsc(any(), any()))
                .thenReturn(items);

        int from = 0;
        int size = 1;

        assertThat(itemService.getAllItemsOfOwner(user1.getId(), from, size)).isEqualTo(items);
    }

    @Test
    public void getAllItemsOfOwnerWhenParamsIncorrectThenThrowInvalidParameterExceptionTest() {

        int from = -1;
        int size = 1;

        assertThrows(InvalidParameterException.class, () ->
                itemService.getAllItemsOfOwner(user1.getId(), from, size));
    }

    @Test
    public void getItemsByNameOrDescriptionWhenParamsOKThenReturnItemsTest() {
        List<Item> items = new ArrayList<>();
        items.add(item1);

        when(itemRepository.findAllItemsByNameOrDescriptionContainingIgnoreCase(any(), any()))
                .thenReturn(items);

        int from = 0;
        int size = 1;

        assertThat(itemService.getItemsByNameOrDescription("suxx", from, size)).isEqualTo(items);
    }

    @Test
    public void getItemsByNameOrDescriptionWhenParamsIncorrectThenThrowInvalidParameterExceptionTest() {
        int from = 0;
        int size = 0;

        assertThrows(InvalidParameterException.class, () ->
                itemService.getItemsByNameOrDescription("suxx", from, size));
    }

    @Test
    public void getItemsByNameOrDescriptionWhenTextIsEmptyThenReturnEmptyListTest() {
        int from = 0;
        int size = 1;

        assertThat(itemService.getItemsByNameOrDescription("", from, size)).isEmpty();
    }

    @Test
    public void getItemsByRequestIdThenReturnItemsTest() {
        List<Item> items = new ArrayList<>();
        items.add(item1);

        when(itemRepository.findAllByRequestId(any()))
                .thenReturn(items);

        assertThat(itemService.getItemsByRequestId(itemRequest1.getId())).isEqualTo(items);
    }
}