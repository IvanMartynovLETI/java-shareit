package ru.practicum.shareit.booking.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.i.api.BookingRepository;
import ru.practicum.shareit.booking.i.api.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.EntityUnavailableException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.user.i.api.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private static final Long TIME_GAP_BETWEEN_BOOKINGS_IN_SECONDS = 1L;

    @Transactional
    @Override
    public Booking createBooking(Booking booking) {
        log.info("Service layer: create booking for item with id: '{}'.", booking.getItem().getId());

        if (booking.getItem().getAvailable().equals(false)) {
            String bookingWarning = "Item with id: " + booking.getItem().getId() + " unavailable for booking.";
            throw new EntityUnavailableException(bookingWarning);
        }
        if (userService.getUserById(booking.getBooker().getId()) == null) {
            String userWarning = "User with id: " + booking.getBooker().getId() + " doesn't exist in database.";
            throw new EntityDoesNotExistException(userWarning);
        }
        if (booking.getItem().getOwner().getId().equals(booking.getBooker().getId())) {
            String bookingWarning = "It's impossible for user with id: " + booking.getBooker().getId() +
                    " to book his own item with id: " + booking.getItem().getId() + ".";
            throw new EntityDoesNotExistException(bookingWarning);
        }

        List<Booking> bookings = repository.findAllByItemIdAndStatusOrderByStartDesc(booking.getItem().getId(),
                Status.APPROVED);

        if (!(bookings == null || bookings.isEmpty())) {
            if (!bookingValidator(bookings, booking)) {
                String bookingWarning = "Item with id: " + booking.getItem().getId() +
                        " not available for booking.";
                throw new EntityDoesNotExistException(bookingWarning);
            }
        }

        return repository.save(booking);
    }

    @Transactional
    @Override
    public Booking updateBooking(Long bookingId, Long userId, Boolean approved) {
        log.info("Service layer: update booking with id: '{}'.", bookingId);

        Booking bookingFromDataBase = repository.findBookingById(bookingId);

        if (!bookingFromDataBase.getItem().getOwner().getId().equals(userId)) {
            String userWarning = "User with id: " + userId + " isn't an owner of item with id: " +
                    bookingFromDataBase.getItem().getId();
            throw new EntityDoesNotExistException(userWarning);
        }
        if (bookingFromDataBase.getStatus().equals(Status.APPROVED)) {
            String bookingWarning = "Booking with id: " + bookingId + " is already approved by it's owner with id: " +
                    userId;
            throw new EntityUnavailableException(bookingWarning);
        }
        if (approved.equals(true)) {
            bookingFromDataBase.setStatus(Status.APPROVED);
        } else {
            bookingFromDataBase.setStatus(Status.REJECTED);
        }

        return repository.save(bookingFromDataBase);
    }

    @Override
    public Booking getBookingInfo(Long bookingId, Long userId) {
        log.info("Service layer: get info about booking with id: '{}'.", bookingId);

        String bookingWarning;

        if (repository.findBookingById(bookingId) == null) {
            bookingWarning = "Booking with id: " + bookingId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(bookingWarning);
        }
        if (!(repository.findBookingById(bookingId).getBooker().getId().equals(userId) ||
                repository.findBookingById(bookingId).getItem().getOwner().getId().equals(userId))) {
            bookingWarning = "No such entity.";
            throw new EntityDoesNotExistException(bookingWarning);
        }

        return repository.findBookingById(bookingId);
    }

    @Override
    public List<Booking> getAllBookingsByUser(Long userId, String state, Integer from, Integer size) {
        LocalDateTime ndtm = LocalDateTime.now();

        log.info("Service layer: get all bookings by user with id: '{}'.", userId);

        if (from < 0 || size < 1) {
            throw new InvalidParameterException("One or more of 'from' or 'size' parameters are incorrect");
        }

        if (userService.getUserById(userId) == null) {
            String userWarning = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(userWarning);
        }

        Integer totalRecords = repository.countAllByBookerId(userId);

        size = Math.min(size, totalRecords - from);
        Pageable page = PageRequest.of(from, size);

        switch (state) {
            case "ALL": {
                return repository.findAllByBookerIdOrderByStartDesc(userId, page);
            }
            case "CURRENT": {
                return repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(userId,
                        ndtm, ndtm, page);
            }
            case "PAST": {
                return repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, ndtm, page);
            }
            case "FUTURE": {
                return repository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, ndtm, page);
            }
            case "WAITING": {
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
            }
            case "REJECTED": {
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
            }
            default: {
                throw new InvalidParameterException("Unknown state: " + state);
            }
        }
    }

    @Override
    public List<Booking> getAllBookingsByOwner(Long ownerId, String state, Integer from, Integer size) {
        LocalDateTime ndtm = LocalDateTime.now();

        log.info("Service layer: get all bookings by owner with id: '{}'.", ownerId);

        if (from < 0 || size < 1) {
            throw new InvalidParameterException("One or more of 'from' or 'size' parameters are incorrect");
        }

        if (userService.getUserById(ownerId) == null) {
            String userWarning = "User with id: " + ownerId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(userWarning);
        }

        Pageable page = PageRequest.of(from, size);

        switch (state) {
            case "ALL": {
                return repository.findAllByItemOwnerIdOrderByStartDesc(ownerId, page);
            }
            case "CURRENT": {
                return repository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, ndtm,
                        ndtm, page);
            }
            case "PAST": {
                return repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, ndtm, page);
            }
            case "FUTURE": {
                return repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, ndtm, page);
            }
            case "WAITING": {
                return repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING, page);
            }
            case "REJECTED": {
                return repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED, page);
            }
            default: {
                throw new InvalidParameterException("Unknown state: " + state);
            }
        }
    }

    @Override
    public List<Booking> getListForLastBookingFinding(Long itemId, LocalDateTime dateTime, Status status) {
        return repository.findAllByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId, dateTime, status);
    }

    @Override
    public List<Booking> getListForNextBookingFinding(Long itemId, LocalDateTime dateTime, Status status) {
        return repository.findAllByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, dateTime, status);
    }

    @Override
    public boolean validateCommentAuthor(Long userId, Long itemId) {
        log.info("Service layer: validate author with id: '{}'.", userId);

        return !repository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusOrderByStartDesc(userId, itemId,
                LocalDateTime.now(), Status.APPROVED).isEmpty();
    }

    private Boolean bookingValidator(List<Booking> bookings, Booking booking) {
        boolean isValid = false;

        for (int i = 0; i <= bookings.size(); i++) {
            if (i == 0) {
                if (bookings.get(0).getEnd().plusSeconds(TIME_GAP_BETWEEN_BOOKINGS_IN_SECONDS)
                        .isBefore(booking.getStart())) {
                    isValid = true;
                }
            } else if (i > 0 && i < bookings.size()) {
                if (bookings.get(i - 1).getStart().minusSeconds(TIME_GAP_BETWEEN_BOOKINGS_IN_SECONDS)
                        .isAfter(booking.getEnd()) && bookings.get(i).getEnd()
                        .plusSeconds(TIME_GAP_BETWEEN_BOOKINGS_IN_SECONDS).isBefore(booking.getStart())) {
                    isValid = true;
                }
            } else {
                if (bookings.get(bookings.size() - 1).getStart().minusSeconds(TIME_GAP_BETWEEN_BOOKINGS_IN_SECONDS)
                        .isAfter(booking.getEnd())) {
                    isValid = true;
                }
            }
        }
        return isValid;
    }
}