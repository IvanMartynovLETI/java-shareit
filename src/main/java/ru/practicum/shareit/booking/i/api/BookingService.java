package ru.practicum.shareit.booking.i.api;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking);

    Booking updateBooking(Long bookingId, Long userId, Boolean approved);

    Booking getBookingInfo(Long bookingId, Long userId);

    List<Booking> getAllBookingsByUser(Long userId, String state);

    List<Booking> getAllBookingsByOwner(Long ownerId, String state);

    List<Booking> getListForLastBookingFinding(Long itemId, LocalDateTime dateTime, Status status);

    List<Booking> getListForNextBookingFinding(Long itemId, LocalDateTime dateTime, Status status);

    boolean validateCommentAuthor(Long userId, Long itemId);
}