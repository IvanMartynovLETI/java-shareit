package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.i.api.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingDtoMapper bookingDtoMapper;

    @PostMapping
    public BookingDtoResponse createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingDtoRequest dtoForBookingRequest) {

        log.info("Controller layer: request for booking creation obtained.");

        return bookingDtoMapper.bookingToDtoResponse(bookingService.createBooking(bookingDtoMapper
                .dtoRequestToBooking(dtoForBookingRequest, userId)), userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable final Long bookingId,
            @RequestParam Boolean approved) {

        log.info("Controller layer: request for booking with id: '{}' update obtained.", bookingId);

        return bookingDtoMapper.bookingToDtoResponse(bookingService.updateBooking(bookingId, userId,
                approved), userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingInfo(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable final Long bookingId) {

        log.info("Controller layer: request for info about booking with id: '{}' update obtained.", bookingId);

        return bookingDtoMapper.bookingToDtoResponse(bookingService.getBookingInfo(bookingId, userId),
                userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "25", required = false) Integer size) {

        log.info("Controller layer: request for all bookings with state: '{}' of user with id: '{}' obtained.",
                state, userId);

        return bookingDtoMapper.bookingsToDtosResponse(bookingService.getAllBookingsByUser(userId, state, from, size),
                userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "25", required = false) Integer size) {

        log.info("Controller layer: request for all bookings with state: '{}' of owner with id: '{}' obtained.",
                state, userId);

        return bookingDtoMapper.bookingsToDtosResponse(bookingService.getAllBookingsByOwner(userId, state, from, size),
                userId);
    }
}