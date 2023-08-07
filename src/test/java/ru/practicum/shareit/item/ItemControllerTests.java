package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.comment.i.api.CommentService;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.i.api.ItemService;
import ru.practicum.shareit.item.model.Item;
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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {
    private static User user1;
    private static Item item1;
    private static ItemDtoResponse itemDtoResponse1;
    private static Comment comment1;
    private static CommentDto commentDto1;
    private static ItemDtoRequest itemDtoRequest1;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemService itemService;

    @MockBean
    ItemDtoMapper itemDtoMapper;

    @MockBean
    CommentService commentService;

    @MockBean
    CommentDtoMapper commentDtoMapper;

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

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription(item1.getDescription());
        itemRequest1.setRequestor(user1);
        LocalDateTime ndtm = LocalDateTime.now();
        itemRequest1.setCreated(ndtm);
        item1.setRequest(itemRequest1);

        comment1 = new Comment();
        comment1.setId(1L);
        comment1.setItem(item1);
        comment1.setText("comment1");
        comment1.setAuthor(user1);
        comment1.setCreated(ndtm);

        commentDto1 = new CommentDto();
        commentDto1.setId(comment1.getId());
        commentDto1.setText(comment1.getText());
        commentDto1.setItemId(comment1.getItem().getId());
        commentDto1.setItemName(comment1.getItem().getName());
        commentDto1.setCreated(comment1.getCreated());
        List<CommentDto> commentDtos = new ArrayList<>();
        commentDtos.add(commentDto1);

        itemDtoResponse1 = new ItemDtoResponse();
        itemDtoResponse1.setId(itemRequest1.getId());
        itemDtoResponse1.setDescription(itemRequest1.getDescription());
        itemDtoResponse1.setName(item1.getName());
        itemDtoResponse1.setAvailable(item1.getAvailable());
        itemDtoResponse1.setComments(commentDtos);
        itemDtoResponse1.setRequestId(itemRequest1.getId());

        itemDtoRequest1 = new ItemDtoRequest();
        itemDtoRequest1.setId(1L);
        itemDtoRequest1.setDescription(item1.getDescription());
        itemDtoRequest1.setName(item1.getName());
        itemDtoRequest1.setAvailable(item1.getAvailable());
        itemDtoRequest1.setRequestId(itemRequest1.getId());
    }

    @SneakyThrows
    @Test
    public void createItemWhenUserExistsThenReturnItemTest() {
        when(itemDtoMapper.itemDtoRequestToItem(any()))
                .thenReturn(item1);

        when(itemService.saveItem(any(), any()))
                .thenReturn(item1);

        when(itemDtoMapper.itemToItemDtoResponse(any(), any()))
                .thenReturn(itemDtoResponse1);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoRequest1))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(item1.getRequest().getId()), Long.class));
    }

    @SneakyThrows
    @Test
    public void createItemWhenUserNotExistThenReturn404ErrorCodeTest() {
        when(itemDtoMapper.itemDtoRequestToItem(any()))
                .thenReturn(item1);

        when(itemService.saveItem(any(), any()))
                .thenReturn(item1);

        when(itemDtoMapper.itemToItemDtoResponse(any(), any()))
                .thenThrow(new EntityDoesNotExistException("User doesn't exist in database."));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoRequest1))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void updateItemWhenUserExistsThenReturnItemTest() {
        when(itemDtoMapper.itemDtoRequestToItem(any()))
                .thenReturn(item1);

        when(itemService.updateItem(any(), any(), any()))
                .thenReturn(item1);

        when(itemDtoMapper.itemToItemDtoResponse(any(), any()))
                .thenReturn(itemDtoResponse1);

        mvc.perform(patch("/items/{itemId}", item1.getId())
                        .content(mapper.writeValueAsString(itemDtoRequest1))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(item1.getRequest().getId()), Long.class));
    }

    @SneakyThrows
    @Test
    public void updateItemWhenUserNotExistThenReturn404ErrorCodeTest() {
        when(itemDtoMapper.itemDtoRequestToItem(any()))
                .thenReturn(item1);

        when(itemService.updateItem(any(), any(), any()))
                .thenReturn(item1);

        when(itemDtoMapper.itemToItemDtoResponse(any(), any()))
                .thenThrow(new EntityDoesNotExistException("User doesn't exist in database."));

        mvc.perform(patch("/items/{itemId}", item1.getId())
                        .content(mapper.writeValueAsString(itemDtoRequest1))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void getItemWhenUserExistsThenReturnItemTest() {
        when(itemDtoMapper.itemDtoRequestToItem(any()))
                .thenReturn(item1);

        when(itemService.getItemById(any()))
                .thenReturn(item1);

        when(itemDtoMapper.itemToItemDtoResponse(any(), any()))
                .thenReturn(itemDtoResponse1);

        mvc.perform(get("/items/{itemId}", item1.getId())
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(item1.getRequest().getId()), Long.class));
    }

    @SneakyThrows
    @Test
    public void getItemWhenItemNotExistThenReturnItemTest() {
        when(itemDtoMapper.itemDtoRequestToItem(any()))
                .thenReturn(item1);

        when(itemService.updateItem(any(), any(), any()))
                .thenReturn(item1);

        when(itemDtoMapper.itemToItemDtoResponse(any(), any()))
                .thenThrow(new EntityDoesNotExistException("Item doesn't exist."));

        mvc.perform(get("/items/{itemId}", item1.getId())
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void getAllItemsOfOwnerWhenParamsOKThenReturnItemsTest() {
        List<ItemDtoResponse> itemDtoResponses = new ArrayList<>();
        itemDtoResponses.add(itemDtoResponse1);

        when(itemService.getAllItemsOfOwner(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(itemDtoMapper.itemsToDtos(any(), any()))
                .thenReturn(itemDtoResponses);

        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "25"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(mapper.writeValueAsString(itemDtoResponses)).isEqualTo(response);
    }

    @SneakyThrows
    @Test
    public void getAllItemsOfOwnerWhenParamsIncorrectThenReturn400ErrorCodeTest() {
        when(itemService.getAllItemsOfOwner(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(itemDtoMapper.itemsToDtos(any(), any()))
                .thenThrow(new InvalidParameterException("Incorrect parameters."));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "25"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void getItemsByNameOrDescriptionWhenParamsOKThenReturnItemsTest() {
        List<ItemDtoResponse> itemDtoResponses = new ArrayList<>();
        itemDtoResponses.add(itemDtoResponse1);
        List<Item> items = new ArrayList<>();
        items.add(item1);

        when(itemDtoMapper.itemsToDtos(any(), any()))
                .thenReturn(itemDtoResponses);

        when(itemService.getItemsByNameOrDescription(any(), any(), any()))
                .thenReturn(items);

        String response = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "0")
                        .param("text", "suxx"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(mapper.writeValueAsString(itemDtoResponses)).isEqualTo(response);
    }

    @SneakyThrows
    @Test
    public void getItemsByNameOrDescriptionWhenParamsIncorrectThenReturn400ErrorCodeTest() {
        List<Item> items = new ArrayList<>();
        items.add(item1);

        when(itemDtoMapper.itemsToDtos(any(), any()))
                .thenThrow(new InvalidParameterException("Incorrect parameters."));

        when(itemService.getItemsByNameOrDescription(any(), any(), any()))
                .thenReturn(items);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "0")
                        .param("text", "suxx"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void createCommentWhenAuthorCapableToPostCommentThenReturnCommentTest() {
        when(commentDtoMapper.commentDtoToComment(any(), any(), any()))
                .thenReturn(comment1);

        when(commentService.saveComment(any()))
                .thenReturn(comment1);

        when(commentDtoMapper.commentToDto(any()))
                .thenReturn(commentDto1);

        String response = mvc.perform(post("/items/{itemId}/comment", item1.getId())
                        .content(mapper.writeValueAsString(commentDto1))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(mapper.writeValueAsString(commentDto1)).isEqualTo(response);
    }

    @SneakyThrows
    @Test
    public void createCommentWhenAuthorNotCapableToPostCommentThenReturn400ErrorCodeTest() {
        when(commentDtoMapper.commentDtoToComment(any(), any(), any()))
                .thenReturn(comment1);

        when(commentService.saveComment(any()))
                .thenReturn(comment1);

        when(commentDtoMapper.commentToDto(any()))
                .thenThrow(new InvalidParameterException("Author not capable to post" +
                        " comment for this item."));

        mvc.perform(post("/items/{itemId}/comment", item1.getId())
                        .content(mapper.writeValueAsString(commentDto1))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}