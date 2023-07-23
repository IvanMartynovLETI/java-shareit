package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.i.api.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingDtoMapper bookingDtoMapper;

    @PostMapping
    public BookingDtoResponse createBooking(@Valid @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                            @Valid @RequestBody BookingDtoRequest dtoForBookingRequest) {
        log.info("Controller layer: request for booking creation obtained.");

        return bookingDtoMapper.bookingToDtoResponse(bookingService.createBooking(bookingDtoMapper
                .dtoRequestToBooking(dtoForBookingRequest, userId)), userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(@Valid @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                      @PathVariable final Long bookingId,
                                                      @RequestParam Boolean approved) {
        log.info("Controller layer: request for booking with id: '{}' update obtained.", bookingId);

        return bookingDtoMapper.bookingToDtoResponse(bookingService.updateBooking(bookingId, userId,
                approved), userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingInfo(@Valid @RequestHeader("X-Sharer-User-Id")
                                                       @NotNull Long userId,
                                                       @PathVariable final Long bookingId) {
        log.info("Controller layer: request for info about booking with id: '{}' update obtained.", bookingId);

        return bookingDtoMapper.bookingToDtoResponse(bookingService.getBookingInfo(bookingId, userId),
                userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsByUser(@Valid @RequestHeader("X-Sharer-User-Id")
                                                                   @NotNull Long userId,
                                                                   @RequestParam(required = false,
                                                                           defaultValue = "ALL")
                                                                   String state) {

        log.info("Controller layer: request for all bookings with state: '{}' of user with id: '{}' obtained.",
                state, userId);

        return bookingDtoMapper.bookingsToDtosResponse(bookingService.getAllBookingsByUser(userId, state),
                userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsByOwner(@Valid @RequestHeader("X-Sharer-User-Id")
                                                                    @NotNull Long userId,
                                                                    @RequestParam(required = false,
                                                                            defaultValue = "ALL")
                                                                    String state) {

        log.info("Controller layer: request for all bookings with state: '{}' of owner with id: '{}' obtained.",
                state, userId);

        return bookingDtoMapper.bookingsToDtosResponse(bookingService.getAllBookingsByOwner(userId, state),
                userId);
    }
}