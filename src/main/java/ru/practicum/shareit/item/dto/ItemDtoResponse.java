package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import ru.practicum.shareit.booking.dto.InnerBookingDtoResponse;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.serializer.InnerDtoSerializer;

import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class ItemDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    @JsonSerialize(using = InnerDtoSerializer.class)
    private InnerBookingDtoResponse lastBooking;

    @JsonSerialize(using = InnerDtoSerializer.class)
    private InnerBookingDtoResponse nextBooking;

    private List<CommentDto> comments;
    private Long requestId;
}