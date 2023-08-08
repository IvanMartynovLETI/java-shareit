package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.i.api.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {
    private static ItemRequest itemRequest1;
    private static ItemRequestDto itemRequestDto1;
    private static User user1;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemRequestDtoMapper itemRequestDtoMapper;

    @MockBean
    ItemRequestService itemRequestService;

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

        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(itemRequest1.getId());
        itemRequestDto1.setDescription(itemRequest1.getDescription());
        itemRequestDto1.setUserId(itemRequest1.getRequestor().getId());
        itemRequestDto1.setCreated(itemRequest1.getCreated());

        ItemDtoRequest itemDtoRequest = new ItemDtoRequest();
        itemDtoRequest.setId(item1.getId());
        itemDtoRequest.setName(item1.getName());
        itemDtoRequest.setDescription(item1.getDescription());
        itemDtoRequest.setAvailable(item1.getAvailable());
        itemDtoRequest.setRequestId(itemRequest1.getId());
        List<ItemDtoRequest> itemDtoRequests = new ArrayList<>();
        itemDtoRequests.add(itemDtoRequest);

        itemRequestDto1.setItems(itemDtoRequests);
    }

    @SneakyThrows
    @Test
    public void createItemRequestWhenOKReturnItemRequestTest() {
        when(itemRequestDtoMapper.itemRequestDtoToItemRequest(any(), any()))
                .thenReturn(itemRequest1);

        when(itemRequestService.saveItemRequest(any(), any()))
                .thenReturn(itemRequest1);

        when(itemRequestDtoMapper.itemRequestToDto(any()))
                .thenReturn(itemRequestDto1);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.userId", is(itemRequestDto1.getUserId()), Long.class));
    }

    @SneakyThrows
    @Test
    public void createItemRequestWhenUserNotFoundThenReturn404CodeTest() {
        when(itemRequestDtoMapper.itemRequestDtoToItemRequest(any(), any()))
                .thenReturn(itemRequest1);

        when(itemRequestService.saveItemRequest(any(), any()))
                .thenReturn(itemRequest1);

        when(itemRequestDtoMapper.itemRequestToDto(any()))
                .thenThrow(new EntityDoesNotExistException("User doesn't exist in database"));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void getAllItemRequestsByOwnerWhenOwnerExistsThenReturnItemRequestsTest() {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        itemRequestDtos.add(itemRequestDto1);

        when(itemRequestService.getAllRequestsByOwner(any()))
                .thenReturn(Collections.emptyList());

        when(itemRequestDtoMapper.itemRequestsToDtos(any()))
                .thenReturn(itemRequestDtos);

        String response = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(mapper.writeValueAsString(itemRequestDtos)).isEqualTo(response);
    }

    @SneakyThrows
    @Test
    public void getAllItemRequestsByOwnerWhenOwnerDoesNotExistThanReturn404ErrorCodeTest() {
        when(itemRequestService.getAllRequestsByOwner(any()))
                .thenReturn(Collections.emptyList());

        when(itemRequestDtoMapper.itemRequestsToDtos(any()))
                .thenThrow(new EntityDoesNotExistException("User doesn't exist in database"));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void getAllItemRequestsWhenOwnerExistsThenReturnItemRequestsTest() {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        itemRequestDtos.add(itemRequestDto1);

        when(itemRequestService.findAllRequests(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(itemRequestDtoMapper.itemRequestsToDtos(any()))
                .thenReturn(itemRequestDtos);

        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(mapper.writeValueAsString(itemRequestDtos)).isEqualTo(response);
    }

    @SneakyThrows
    @Test
    public void getAllItemRequestsWhenOwnerDoesNotExistThenReturn404ErrorCodeTest() {
        when(itemRequestService.findAllRequests(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(itemRequestDtoMapper.itemRequestsToDtos(any()))
                .thenThrow(new EntityDoesNotExistException("User doesn't exist in database"));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void getAllItemRequestsWhenIncorrectParametersThenReturn400ErrorCodeTest() {

        when(itemRequestService.findAllRequests(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(itemRequestDtoMapper.itemRequestsToDtos(any()))
                .thenThrow(new InvalidParameterException("One or more of 'from' or 'size' parameters are incorrect"));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void getItemRequestByIdWhenUserAndRequestExistsThenReturnItemRequestTest() {
        when(itemRequestService.getItemRequestByIdWithValidation(any(), any()))
                .thenReturn(itemRequest1);

        when(itemRequestDtoMapper.itemRequestToDto(any()))
                .thenReturn(itemRequestDto1);

        mvc.perform(get("/requests/{requestId}", itemRequest1.getId())
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.userId", is(itemRequestDto1.getUserId()), Long.class));
    }

    @SneakyThrows
    @Test
    public void getItemRequestByIdWhenRequestNotExistsThenReturn404ErrorCodeTest() {
        when(itemRequestService.getItemRequestByIdWithValidation(any(), any()))
                .thenReturn(itemRequest1);

        when(itemRequestDtoMapper.itemRequestToDto(any()))
                .thenThrow(new EntityDoesNotExistException("ItemRequest doesn't exist in database"));

        mvc.perform(get("/requests/{requestId}", itemRequest1.getId())
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isNotFound());
    }
}