package ru.practicum.shareit.booking.dto;

import lombok.*;

import ru.practicum.shareit.constraints.EndTimeAfterStartTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@EndTimeAfterStartTime
public class BookingDtoRequest {

    @Future
    @NotNull
    private LocalDateTime start;

    @Future
    @NotNull
    private LocalDateTime end;

    @NotNull
    private Long itemId;
}