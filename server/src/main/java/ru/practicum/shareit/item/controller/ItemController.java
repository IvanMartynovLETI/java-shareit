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

import java.util.List;

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
                               @RequestBody ItemDtoRequest itemDtoRequest) {
        log.info("Controller layer: request for item creation obtained.");

        return itemDtoMapper.itemToItemDtoResponse(itemService.saveItem(userId,
                itemDtoMapper.itemDtoRequestToItem(itemDtoRequest)), userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponse updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable final Long itemId,
                                      @RequestBody ItemDtoRequest itemDtoRequest) {
        log.info("Controller layer: request for item with id: '{}' update obtained.", itemId);

        return itemDtoMapper.itemToItemDtoResponse(itemService.updateItem(userId, itemId,
                itemDtoMapper.itemDtoRequestToItem(itemDtoRequest)), userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable final Long itemId) {
        log.info("Controller layer: request for getting of item with id: '{}' obtained.", itemId);

        return itemDtoMapper.itemToItemDtoResponse(itemService.getItemById(itemId), userId);
    }

    @GetMapping
    public List<ItemDtoResponse> getAllItemsOfOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "25", required = false) Integer size) {
        log.info("Controller layer: request for getting of all items of owner with id: '{}' obtained.", userId);

        return itemDtoMapper.itemsToDtos(itemService.getAllItemsOfOwner(userId, from, size), userId);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> getItemsByNameOrDescription(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "25", required = false) Integer size,
            @RequestParam String text) {
        log.info("Controller layer: request for search items by name or description obtained.");

        return itemDtoMapper.itemsToDtos(itemService.getItemsByNameOrDescription(text, from, size), userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable final Long itemId,
                                    @RequestBody CommentDto commentDto) {
        log.info("Controller layer: request for create comment of item with id: '{}' from user with id: '{}'",
                itemId, userId);

        return commentDtoMapper.commentToDto(commentService.saveComment(commentDtoMapper
                .commentDtoToComment(commentDto, userId, itemId)));
    }
}