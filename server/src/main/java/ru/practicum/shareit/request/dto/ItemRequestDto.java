package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;

    private String description;
    private Long userId;
    private LocalDateTime created;
    private List<ItemDtoRequest> items;
}