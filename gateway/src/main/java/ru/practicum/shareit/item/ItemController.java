package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemDtoRequest itemDtoRequest) {
        log.info("ShareIt gateway: request for item creation obtained.");

        return itemClient.add(userId, itemDtoRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Valid @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                      @PathVariable final Long itemId,
                                      @RequestBody ItemDtoRequest itemDtoRequest) {
        log.info("ShareIt gateway: request for item with id: '{}' update obtained.", itemId);

        return itemClient.updateItem(userId, itemId, itemDtoRequest);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Valid @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                       @PathVariable final Long itemId) {
        log.info("ShareIt gateway: request for getting of item with id: '{}' obtained.", itemId);

        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfOwner(
            @Valid @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "25", required = false) Integer size) {
        log.info("ShareIt gateway: request for getting of all items of owner with id: '{}' obtained.", userId);

        return itemClient.getAllItemsOfOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByNameOrDescription(
            @Valid @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "25", required = false) Integer size,
            @RequestParam String text) {
        log.info("ShareIt gateway: request for search items by name or description obtained.");

        return itemClient.getItemsByNameOrDescription(userId, from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                    @PathVariable final Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("ShareIt gateway: request for create comment of item with id: '{}' from user with id: '{}'",
                itemId, userId);

        return itemClient.createComment(userId, itemId, commentDto);
    }
}