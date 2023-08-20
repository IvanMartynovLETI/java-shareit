package ru.practicum.shareit.booking.dto;

import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class InnerBookingDtoResponse {
    private Long id;
    private Long bookerId;
    private String name;
}