package ru.practicum.shareit.item.i.api;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item saveItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    Item getItemById(Long id);

    List<Item> getAllItemsOfOwner(Long userId, Integer from, Integer size);

    List<Item> getItemsByNameOrDescription(String text, Integer from, Integer size);

    List<Item> getItemsByRequestId(Long requestId);
}