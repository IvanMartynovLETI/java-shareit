package ru.practicum.shareit.booking.i.api;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findBookingById(Long id);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime dateTime1,
                                                                             LocalDateTime dateTime2);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime1,
                                                                                LocalDateTime dateTime2);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findAllByItemIdAndStatusOrderByStartDesc(Long itemId, Status status);

    List<Booking> findAllByBookerIdAndItemIdAndEndBeforeAndStatusOrderByStartDesc(Long bookerId, Long itemId,
                                                                                  LocalDateTime dateTime,
                                                                                  Status status);

    List<Booking> findAllByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime dateTime,
                                                                       Status status);

    List<Booking> findAllByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime dateTime,
                                                                       Status status);
}