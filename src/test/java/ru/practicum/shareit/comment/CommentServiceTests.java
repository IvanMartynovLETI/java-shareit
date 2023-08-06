package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.i.api.BookingService;
import ru.practicum.shareit.comment.i.api.CommentRepository;
import ru.practicum.shareit.comment.i.impl.CommentServiceImpl;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {
    private static User user1;
    private static Item item1;
    private static Comment comment;

    @InjectMocks
    CommentServiceImpl commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingService bookingService;

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
    public void saveComment_WhenAuthorIsAbleToPostComment_ThenReturnCommentTest() {
        when(bookingService.validateCommentAuthor(user1.getId(), item1.getId()))
                .thenReturn(true);

        when(commentRepository.save(comment))
                .thenReturn(comment);

        assertThat(commentService.saveComment(comment)).isEqualTo(comment);
    }

    @Test
    public void saveComment_WhenAuthorNotAbleToPostComment_ThenThrowInvalidParameterExceptionTest() {
        when(bookingService.validateCommentAuthor(user1.getId(), item1.getId()))
                .thenReturn(false);

        assertThrows(InvalidParameterException.class, () ->
                commentService.saveComment(comment));
    }

    @Test
    public void getAllCommentsByItemId_ThenReturnCommentsTest() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        when(commentRepository.findAllByItemIdOrderByCreatedDesc(item1.getId()))
                .thenReturn(comments);

        assertThat(commentService.getAllCommentsByItemId(item1.getId())).isEqualTo(comments);
    }
}