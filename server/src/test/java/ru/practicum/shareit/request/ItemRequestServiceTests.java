package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.i.api.ItemRequestRepository;
import ru.practicum.shareit.request.i.impl.ItemRequestServiceImpl;
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
public class ItemRequestServiceTests {
    private static User user1;
    private static ItemRequest itemRequest1;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserService userService;

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
    public void createItemRequestWhenUserExistsThenReturnItemRequestTest() {
        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest1);

        assertThat(itemRequestService.saveItemRequest(user1.getId(), itemRequest1)).isEqualTo(itemRequest1);
    }

    @Test
    public void createItemRequestWhenUserNotExistThenThrowEntityDoesNotExistExceptionTest() {
        when(userService.getUserById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> itemRequestService.saveItemRequest(user1.getId(),
                itemRequest1));
    }

    @Test
    public void getItemRequestByIdThenReturnItemTest() {
        when(itemRequestRepository.findItemRequestById(any()))
                .thenReturn(itemRequest1);

        assertThat(itemRequestService.getItemRequestById(itemRequest1.getId())).isEqualTo(itemRequest1);
    }

    @Test
    public void getItemRequestByIdWhenValidationOKThenReturnItemRequestTest() {
        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(itemRequestRepository.findItemRequestById(any()))
                .thenReturn(itemRequest1);

        assertThat(itemRequestService.getItemRequestByIdWithValidation(user1.getId(), itemRequest1.getId()))
                .isEqualTo(itemRequest1);
    }

    @Test
    public void getItemRequestByIdWhenUserNotExistThenThrowEntityDoesNotExistExceptionTest() {
        when(userService.getUserById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> itemRequestService
                .getItemRequestByIdWithValidation(user1.getId(), itemRequest1.getId()));
    }

    @Test
    public void getItemRequestByIdWhenItemRequestNotExistThenThrowEntityDoesNotExistExceptionTest() {
        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(itemRequestRepository.findItemRequestById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> itemRequestService
                .getItemRequestByIdWithValidation(user1.getId(), itemRequest1.getId()));
    }

    @Test
    public void getAllItemRequestsByOwnerWhenOwnerExistsThenReturnItemRequestTest() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest1);

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(any()))
                .thenReturn(itemRequests);

        assertThat(itemRequestService.getAllRequestsByOwner(user1.getId())).isEqualTo(itemRequests);
    }

    @Test
    public void getAllItemRequestsByOwnerWhenOwnerNotExistThenThrowEntityDoesNotExistExceptionTest() {
        when(userService.getUserById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> itemRequestService.getAllRequestsByOwner(user1.getId()));
    }

    @Test
    public void findAllRequestsWhenAllOKThenReturnItemRequestsTest() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest1);

        int from = 0;
        int size = 1;

        Pageable pageable = PageRequest.of(from, size);
        Page<ItemRequest> page = new PageImpl<>(itemRequests, pageable, itemRequests.size());

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(itemRequestRepository.findAllByRequestorIdNotIn(any(), any()))
                .thenReturn(page);

        assertThat(itemRequestService.findAllRequests(user1.getId(), from, size)).isEqualTo(itemRequests);
    }

    @Test
    public void findAllRequestsWhenUserNotEExistThenThrowEntityDoesNotExistExceptionTest() {
        when(userService.getUserById(user1.getId()))
                .thenReturn(null);

        int from = 0;
        int size = 1;

        assertThrows(EntityDoesNotExistException.class, () -> itemRequestService.findAllRequests(user1.getId(), from,
                size));
    }

    @Test
    public void findAllRequestsWhenIncorrectParamsThenThrowInvalidParameterExceptionTest() {
        when(userService.getUserById(any()))
                .thenReturn(user1);

        int from = -1;
        int size = 1;

        assertThrows(InvalidParameterException.class, () -> itemRequestService.findAllRequests(user1.getId(), from,
                size));
    }

    @Test
    public void findAllRequestsWhenAllOKAndEmptyListOfRequestsThenReturnEmptyListOfItemRequestsTest() {
        List<ItemRequest> itemRequests = new ArrayList<>();

        int from = 0;
        int size = 1;

        Pageable pageable = PageRequest.of(from, size);
        Page<ItemRequest> page = new PageImpl<>(itemRequests, pageable, 0);

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(itemRequestRepository.findAllByRequestorIdNotIn(any(), any()))
                .thenReturn(page);

        assertThat(itemRequestService.findAllRequests(user1.getId(), from, size)).isEmpty();
    }
}