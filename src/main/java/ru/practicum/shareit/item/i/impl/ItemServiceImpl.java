package ru.practicum.shareit.item.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.i.api.ItemRepository;
import ru.practicum.shareit.item.i.api.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;

    @Transactional
    @Override
    public Item saveItem(Long userId, Item item) {
        log.info("Service layer: create item with name: '{}'.", item.getName());

        User owner = userService.getUserById(userId);

        if (owner == null) {
            String userWarning = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(userWarning);
        }

        item.setOwner(owner);

        return repository.save(item);
    }

    @Transactional
    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        log.info("Service layer: update item with id: '{}'.", itemId);

        Item itemFromDataBase = repository.findItemById(itemId);
        User userFromDataBase = userService.getUserById(userId);

        if (userFromDataBase == null) {
            String userWarning = "User with id: " + userId + " doesn't present in database.";
            throw new EntityDoesNotExistException(userWarning);
        }

        if (item.getName() != null) {
            itemFromDataBase.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemFromDataBase.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromDataBase.setAvailable(item.getAvailable());
        }

        return repository.save(itemFromDataBase);
    }

    @Override
    public Item getItemById(Long id) {
        log.info("Service layer: get item by id: '{}'.", id);

        Item item = repository.findItemById(id);

        if (item == null) {
            String itemWarning = "Item with id: " + id + " doesn't exist in database.";
            throw new EntityDoesNotExistException(itemWarning);
        }

        return item;
    }

    @Override
    public List<Item> getAllItemsOfOwner(Long userId, Integer from, Integer size) {
        log.info("Service layer: get all items of owner with id: '{}'.", userId);

        if (from < 0 || size < 1) {
            throw new InvalidParameterException("One or more of 'from' or 'size' parameters are incorrect");
        }

        Pageable page = PageRequest.of(from, size);

        return repository.findAllItemsByOwner(userService.getUserById(userId), page);
    }

    @Override
    public List<Item> getItemsByNameOrDescription(String text, Integer from, Integer size) {
        log.info("Service layer: get items by name or description.");

        if (from < 0 || size < 1) {
            throw new InvalidParameterException("One or more of 'from' or 'size' parameters are incorrect");
        }

        Pageable page = PageRequest.of(from, size);

        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return repository.findAllItemsByNameOrDescriptionContainingIgnoreCase(text, page);
        }
    }

    @Override
    public List<Item> getItemsByRequestId(Long requestId) {
        log.info("Service layer: get items by request with id: '{}'.", requestId);

        return repository.findAllByRequestId(requestId);
    }
}