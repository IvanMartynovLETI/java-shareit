package ru.practicum.shareit.request.i.api;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest saveItemRequest(Long userId, ItemRequest itemRequest);

    ItemRequest getItemRequestById(Long requestId);

    ItemRequest getItemRequestByIdWithValidation(Long userId, Long requestId);

    List<ItemRequest> getAllRequestsByOwner(Long userId);

    List<ItemRequest> findAllRequests(Long userId, Integer from, Integer size);
}