package ru.practicum.shareit.item.dto;

import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoRequest {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}