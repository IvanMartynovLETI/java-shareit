package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("ShareIt gateway: request for itemRequest creation obtained.");

        return requestClient.add(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByOwner(@RequestHeader("X-Sharer-User-Id")
                                                      Long userId) {
        log.info("ShareIt gateway: request for obtaining all itemRequests for owner with id: '{}'.", userId);

        return requestClient.getAllRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsWithPagination(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "25", required = false) Integer size) {
        log.info("ShareIt gateway: request for obtaining all requests for user with id: '{}' " +
                "with pagination.", userId);

        return requestClient.getAllRequestsWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        log.info("ShareIt gateway: request for obtaining request with id: '{}' from user with id: '{}'.", requestId,
                userId);

        return requestClient.getItemRequestById(requestId, userId);
    }
}