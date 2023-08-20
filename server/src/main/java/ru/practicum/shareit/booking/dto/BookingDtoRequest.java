package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BookingDtoRequest {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}