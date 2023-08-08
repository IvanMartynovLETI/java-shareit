package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.i.api.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemRequestDtoMapper itemRequestDtoMapper;

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Controller layer: request for itemRequest creation obtained.");

        return itemRequestDtoMapper.itemRequestToDto(itemRequestService.saveItemRequest(userId,
                itemRequestDtoMapper.itemRequestDtoToItemRequest(userId, itemRequestDto)));
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByOwner(@RequestHeader("X-Sharer-User-Id")
                                                      Long userId) {
        log.info("Controller layer: request for obtaining all itemRequests for owner with id: '{}'.", userId);

        return itemRequestDtoMapper.itemRequestsToDtos(itemRequestService.getAllRequestsByOwner(userId));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsWithPagination(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "25", required = false) Integer size) {
        log.info("Controller layer: request for obtaining all requests for user with id: '{}' " +
                "with pagination.", userId);

        return itemRequestDtoMapper.itemRequestsToDtos(itemRequestService.findAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        log.info("Controller layer: request for obtaining request with id: '{}' from user with id: '{}'.", requestId,
                userId);

        return itemRequestDtoMapper.itemRequestToDto(itemRequestService.getItemRequestByIdWithValidation(userId,
                requestId));
    }
}