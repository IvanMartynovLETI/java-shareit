package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.serializer.ItemSerializer;
import ru.practicum.shareit.serializer.UserSerializer;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class BookingDtoResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;

    @JsonSerialize(using = UserSerializer.class)
    private User booker;

    @JsonSerialize(using = ItemSerializer.class)
    private Item item;
    private String name;
}