package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class ItemDtoMapper {
    public Optional<ItemDto> itemToItemDto(Item item) {
        if (item == null) {
            return Optional.empty();
        } else {
            return Optional.of(convertItemToItemDto(item));
        }
    }

    public Item itemDtoToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        return item;
    }

    public Optional<List<ItemDto>> itemsToDtos(List<Item> items) {
        if (items == null) {
            return Optional.empty();
        } else {
            return Optional.of(items
                    .stream()
                    .map(this::convertItemToItemDto)
                    .collect(Collectors.toList()));
        }
    }

    public ItemDto convertItemToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        return itemDto;
    }
}