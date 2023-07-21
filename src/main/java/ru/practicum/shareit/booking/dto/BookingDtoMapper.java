package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.i.api.ItemService;
import ru.practicum.shareit.user.i.api.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingDtoMapper {
    private final UserService userService;
    private final ItemService itemService;

    public Optional<BookingDtoResponse> bookingToDtoForBookingResponse(Booking booking, Long userId) {
        if (booking == null) {
            return Optional.empty();
        } else {
            return Optional.of(bookingToDtoResponse(booking, userId));
        }
    }

    public Booking dtoRequestToBooking(BookingDtoRequest dtoForBookingRequest, Long userId) {
        Booking booking = new Booking();
        booking.setStart(dtoForBookingRequest.getStart());
        booking.setEnd(dtoForBookingRequest.getEnd());
        booking.setBooker(userService.getUserById(userId));
        booking.setItem(itemService.getItemById(dtoForBookingRequest.getItemId()));
        booking.setStatus(Status.WAITING);

        return booking;
    }

    public Optional<List<BookingDtoResponse>> bookingsToDtosResponse(List<Booking> bookings, Long userId) {
        if (bookings == null) {
            return Optional.empty();
        } else {
            return Optional.of(bookings
                    .stream()
                    .map((Booking b) -> bookingToDtoResponse(b, userId))
                    .collect(Collectors.toList()));
        }
    }

    public BookingDtoResponse bookingToDtoResponse(Booking booking, Long userId) {
        BookingDtoResponse dtoForBookingResponse = new BookingDtoResponse();
        dtoForBookingResponse.setId(booking.getId());
        dtoForBookingResponse.setStart(booking.getStart());
        dtoForBookingResponse.setEnd(booking.getEnd());
        dtoForBookingResponse.setStatus(booking.getStatus());
        dtoForBookingResponse.setName(booking.getItem().getName());

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            dtoForBookingResponse.setBooker(booking.getBooker());
            dtoForBookingResponse.setItem(booking.getItem());
        } else {
            dtoForBookingResponse.setBooker(null);
            dtoForBookingResponse.setItem(null);
        }

        return dtoForBookingResponse;
    }
}