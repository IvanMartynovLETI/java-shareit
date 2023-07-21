package ru.practicum.shareit.comment.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.i.api.BookingService;
import ru.practicum.shareit.comment.i.api.CommentRepository;
import ru.practicum.shareit.comment.i.api.CommentService;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.InvalidParameterException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final BookingService bookingService;

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        log.info("Service layer: create comment for item with id: '{}' from user with id: '{}'.",
                comment.getItem().getId(), comment.getAuthor().getId());

        if (!bookingService.validateCommentAuthor(comment.getAuthor().getId(), comment.getItem().getId())) {
            String commentWarning = "Author with id: " + comment.getAuthor().getId() +
                    " can't post comment about item with id: " + comment.getItem().getId();
            throw new InvalidParameterException(commentWarning);
        }

        return repository.save(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> getAllCommentsByItemId(Long itemId) {
        log.info("Service layer: get all comments about item with id: '{}'.", itemId);

        return repository.findAllByItemIdOrderByCreatedDesc(itemId);
    }
}