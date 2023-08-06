package ru.practicum.shareit.request.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.request.i.api.ItemRequestRepository;
import ru.practicum.shareit.request.i.api.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;

    @Override
    public ItemRequest saveItemRequest(Long userId, ItemRequest itemRequest) {
        log.info("Service layer: create itemRequest from user with id: '{}'.", userId);

        User user = userService.getUserById(userId);

        if (user == null) {
            String userWarning = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(userWarning);
        }

        itemRequest.setRequestor(user);

        return repository.save(itemRequest);
    }

    @Override
    public ItemRequest getItemRequestById(Long requestId) {
        log.info("Service layer: get itemRequest by id: '{}'.", requestId);

        return repository.findItemRequestById(requestId);
    }

    @Override
    public ItemRequest getItemRequestByIdWithValidation(Long userId, Long requestId) {
        log.info("Service layer: get itemRequest by id: '{}' for user with id: '{}'.", requestId, userId);

        if (userService.getUserById(userId) == null) {
            String userWarning = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(userWarning);
        }

        if (repository.findItemRequestById(requestId) == null) {
            String itemRequestWarning = "ItemRequest with id: " + requestId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(itemRequestWarning);
        }

        return repository.findItemRequestById(requestId);
    }

    @Override
    public List<ItemRequest> getAllRequestsByOwner(Long userId) {
        log.info("Service layer: get all itemRequests of user with id: '{}'.", userId);

        if (userService.getUserById(userId) == null) {
            String userWarning = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(userWarning);
        }

        return repository.findAllByRequestorIdOrderByCreatedDesc(userId);
    }

    @Override
    public List<ItemRequest> findAllRequests(Long userId, Integer from, Integer size) {
        log.info("Service layer: get all requests for user with id: '{}' with pagination", userId);

        if (userService.getUserById(userId) == null) {
            String userWarning = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(userWarning);
        }

        if (from < 0 || size < 1) {
            throw new InvalidParameterException("One or more of 'from' or 'size' parameters are incorrect");
        }

        Pageable page = PageRequest.of(from, size, Sort.by("created").descending());
        List<Long> userIdsToBeExcluded = new ArrayList<>();
        userIdsToBeExcluded.add(userId);

        List<ItemRequest> requests = repository.findAllByRequestorIdNotIn(userIdsToBeExcluded, page).getContent();

        if (requests.isEmpty()) {
            return new ArrayList<>();
        }

        return requests;
    }
}