package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.i.api.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentDtoMapperTests {
    private final CommentDtoMapper commentDtoMapper;
    private static User user1;
    private static Item item1;
    private static Comment comment;

    @BeforeAll
    public static void beforeAll() {
        LocalDateTime ndtm = LocalDateTime.now();

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
        item1.setRequest(itemRequest1);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setItem(item1);
        comment.setAuthor(user1);
        comment.setCreated(ndtm);
    }

    @Test
    public void commentToDtoTest() {
        CommentDto commentDto = commentDtoMapper.commentToDto(comment);

        assertThat(commentDto.getId()).isEqualTo(comment.getId());
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getAuthorName()).isEqualTo(comment.getAuthor().getName());
        assertThat(commentDto.getItemId()).isEqualTo(comment.getItem().getId());
        assertThat(commentDto.getItemName()).isEqualTo(comment.getItem().getName());
        assertThat(commentDto.getCreated()).isEqualTo(comment.getCreated());
    }

    @Test
    public void commentDtoToCommentTest() {
        CommentDto commentDto = commentDtoMapper.commentToDto(comment);

        UserService userService = Mockito.mock(UserService.class);
        Mockito
                .when(userService.getUserById(Mockito.any()))
                .thenReturn(user1);

        ReflectionTestUtils.setField(commentDtoMapper, "userService", userService);

        ItemService itemService = Mockito.mock(ItemService.class);
        Mockito
                .when(itemService.getItemById(Mockito.any()))
                .thenReturn(item1);

        ReflectionTestUtils.setField(commentDtoMapper, "itemService", itemService);

        Comment comment = commentDtoMapper.commentDtoToComment(commentDto, user1.getId(), item1.getId());

        assertThat(comment.getText()).isEqualTo(commentDto.getText());
        assertThat(comment.getAuthor().getName()).isEqualTo(commentDto.getAuthorName());
        assertThat(comment.getItem().getId()).isEqualTo(commentDto.getItemId());
        assertThat(comment.getItem().getName()).isEqualTo(commentDto.getItemName());
    }

    @Test
    public void commentsToDtosTest() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        List<CommentDto> commentDtos = commentDtoMapper.commentsToDtos(comments);

        assertThat(commentDtos.size()).isEqualTo(comments.size());
        assertThat(commentDtos.get(0).getId()).isEqualTo(comments.get(0).getId());
        assertThat(commentDtos.get(0).getText()).isEqualTo(comments.get(0).getText());
        assertThat(commentDtos.get(0).getAuthorName()).isEqualTo(comments.get(0).getAuthor().getName());
        assertThat(commentDtos.get(0).getItemId()).isEqualTo(comments.get(0).getItem().getId());
        assertThat(commentDtos.get(0).getItemName()).isEqualTo(comments.get(0).getItem().getName());
        assertThat(commentDtos.get(0).getCreated()).isEqualTo(comments.get(0).getCreated());
    }
}