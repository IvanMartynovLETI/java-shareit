package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.comment.i.api.CommentService;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.i.api.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemDtoMapper itemDtoMapper;
    private final CommentService commentService;
    private final CommentDtoMapper commentDtoMapper;

    @PostMapping
    public ItemDtoResponse add(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Valid @RequestBody ItemDtoRequest itemDtoRequest) {
        LocalDateTime ndtm = LocalDateTime.now();

        log.info("Controller layer: request for item creation obtained.");

        return itemDtoMapper.convertItemToItemDtoResponse(itemService.saveItem(userId,
                itemDtoMapper.itemDtoRequestToItem(itemDtoRequest)), userId, ndtm);
    }

    @PatchMapping("/{itemId}")
    public Optional<ItemDtoResponse> updateItem(@Valid @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                @PathVariable final Long itemId,
                                                @RequestBody ItemDtoRequest itemDtoRequest) {
        LocalDateTime ndtm = LocalDateTime.now();

        log.info("Controller layer: request for item with id: '{}' update obtained.", itemId);

        return itemDtoMapper.itemToItemDtoResponse(itemService.updateItem(userId, itemId,
                itemDtoMapper.itemDtoRequestToItem(itemDtoRequest)), userId, ndtm);
    }

    @GetMapping("/{itemId}")
    public Optional<ItemDtoResponse> getItemById(@Valid @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                 @PathVariable final Long itemId) {
        LocalDateTime ndtm = LocalDateTime.now();

        log.info("Controller layer: request for getting of item with id: '{}' obtained.", itemId);

        return itemDtoMapper.itemToItemDtoResponse(itemService.getItemById(itemId), userId, ndtm);
    }

    @GetMapping
    public Optional<List<ItemDtoResponse>> getAllItemsOfOwner(@RequestHeader("X-Sharer-User-Id")
                                                              @NotNull Long userId) {
        LocalDateTime ndtm = LocalDateTime.now();

        log.info("Controller layer: request for getting of all items of owner with id: '{}' obtained.", userId);

        return itemDtoMapper.itemsToDtos(itemService.getAllItemsOfOwner(userId), userId, ndtm);
    }

    @GetMapping("/search")
    public Optional<List<ItemDtoResponse>> getItemsByNameOrDescription(@Valid @RequestHeader("X-Sharer-User-Id")
                                                                       @NotNull Long userId,
                                                                       @RequestParam String text) {
        LocalDateTime ndtm = LocalDateTime.now();

        log.info("Controller layer: request for search items by name or description obtained.");

        return itemDtoMapper.itemsToDtos(itemService.getItemsByNameOrDescription(text), userId, ndtm);
    }

    @PostMapping("/{itemId}/comment")
    public Optional<CommentDto> createComment(@Valid @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                              @PathVariable final Long itemId,
                                              @Valid @RequestBody CommentDto commentDto) {
        log.info("Controller layer: request for create comment of item with id: '{}' from user with id: '{}'",
                itemId, userId);

        return commentDtoMapper.commentToDto(commentService.saveComment(commentDtoMapper
                .commentDtoToComment(commentDto, userId, itemId)));
    }
}