package ru.practicum.shareit.comment.i.api;

import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentService {
    Comment saveComment(Comment comment);

    List<Comment> getAllCommentsByItemId(Long itemId);
}