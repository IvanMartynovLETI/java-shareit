package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.i.api.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestDtoMapper {
    private final UserService userService;
    private final ItemDtoMapper itemDtoMapper;

    public ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setUserId(itemRequest.getRequestor().getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        if (itemRequest.getItems() != null) {
            itemRequestDto.setItems(itemsToItemDtoRequests(itemRequest.getItems()));
        }

        return itemRequestDto;
    }

    public ItemRequest itemRequestDtoToItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        LocalDateTime ndtm = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(userService.getUserById(userId));
        itemRequest.setCreated(ndtm);

        if (itemRequestDto.getItems() != null) {
            itemRequest.setItems(itemDtoRequestsToItems(itemRequestDto.getItems()));
        }

        return itemRequest;
    }

    public List<ItemRequestDto> itemRequestsToDtos(List<ItemRequest> itemRequests) {

        return itemRequests
                .stream()
                .map(this::itemRequestToDto)
                .collect(Collectors.toList());
    }

    private ItemDtoRequest itemToItemDtoRequest(Item item) {
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest();
        itemDtoRequest.setId(item.getId());
        itemDtoRequest.setName(item.getName());
        itemDtoRequest.setDescription(item.getDescription());
        itemDtoRequest.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemDtoRequest.setRequestId(item.getRequest().getId());
        }

        return itemDtoRequest;
    }

    private List<ItemDtoRequest> itemsToItemDtoRequests(List<Item> items) {

        return items
                .stream()
                .map(this::itemToItemDtoRequest)
                .collect(Collectors.toList());
    }

    private List<Item> itemDtoRequestsToItems(List<ItemDtoRequest> itemDtoRequests) {
        return itemDtoRequests
                .stream()
                .map(itemDtoMapper::itemDtoRequestToItem)
                .collect(Collectors.toList());
    }
}