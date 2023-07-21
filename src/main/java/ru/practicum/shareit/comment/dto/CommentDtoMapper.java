package ru.practicum.shareit.comment.dto;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.i.api.ItemService;
import ru.practicum.shareit.user.i.api.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentDtoMapper {
    private final UserService userService;
    private final ItemService itemService;

    public Optional<CommentDto> commentToDto(Comment comment) {
        if (comment == null) {
            return Optional.empty();
        } else {
            return Optional.of(convertCommentToDto(comment));
        }
    }

    public CommentDto convertCommentToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setItemId(comment.getItem().getId());
        commentDto.setItemName(comment.getItem().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

    public Comment commentDtoToComment(CommentDto commentDto, Long userId, Long itemId) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(itemService.getItemById(itemId));
        comment.setAuthor(userService.getUserById(userId));
        comment.setCreated(LocalDateTime.now());

        return comment;
    }

    public List<CommentDto> commentsToDtos(List<Comment> comments) {

        return comments
                .stream()
                .map(this::convertCommentToDto)
                .collect(Collectors.toList());
    }
}