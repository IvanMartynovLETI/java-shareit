package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.comment.i.api.CommentService;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CommentServiceIntegrationTests {
    private final CommentService commentService;
    private static Comment comment;

    @BeforeEach
    public void beforeEach() {
        User user1 = new User(1L, "user1", "user1@yandex.ru");
        User user2 = new User(2L, "user2", "user2@yandex.ru");

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description of item1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("comment1");
        comment.setAuthor(user2);
        comment.setItem(item1);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    public void createComment_ThenReturnCommentTest() {
        Comment commentReturned = commentService.saveComment(comment);

        Long id = commentReturned.getId();
        String text = commentReturned.getText();
        LocalDateTime created = commentReturned.getCreated();

        assertThat(id).isEqualTo(comment.getId());
        assertThat(text).isEqualTo(comment.getText());
        assertThat(created).isEqualTo(comment.getCreated());
    }

    @Test
    public void createComment_WhenAuthorNotAbleToPostComment_ThenThrowInvalidParameterExceptionTest() {
        User user3 = new User(3L, "user3", "user3@yandex.ru");
        comment.setAuthor(user3);

        assertThrows(InvalidParameterException.class, () ->
                commentService.saveComment(comment));

        comment.setAuthor(user3);
    }
}