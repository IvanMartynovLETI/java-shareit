package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(USER_ID_HEADER) @NotNull Long userId,
            @Valid @RequestBody BookingDtoRequest dtoForBookingRequest) {

        log.info("ShareIt gateway: request for booking creation obtained.");

        return bookingClient.createBooking(userId, dtoForBookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @RequestHeader(USER_ID_HEADER) @NotNull Long userId,
            @PathVariable final Long bookingId,
            @RequestParam Boolean approved) {

        log.info("ShareIt gateway: request for booking with id: '{}' update obtained.", bookingId);

        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingInfo(
            @RequestHeader(USER_ID_HEADER) @NotNull Long userId,
            @PathVariable final Long bookingId) {

        log.info("ShareIt gateway: request for info about booking with id: '{}' update obtained.", bookingId);

        return bookingClient.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUser(
            @RequestHeader(USER_ID_HEADER) @NotNull Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "25", required = false) Integer size) {

        log.info("ShareIt gateway: request for all bookings with state: '{}' of user with id: '{}' obtained.",
                state, userId);

        return bookingClient.getAllBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(
            @RequestHeader(USER_ID_HEADER) @NotNull Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "25", required = false) Integer size) {

        log.info("ShareIt gateway: request for all bookings with state: '{}' of owner with id: '{}' obtained.",
                state, userId);

        return bookingClient.getAllBookingsByOwner(userId, state, from, size);
    }
}