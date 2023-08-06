package ru.practicum.shareit.booking.i.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findBookingById(Long id);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable page);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime dateTime1,
                                                                             LocalDateTime dateTime2, Pageable page);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable page);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable page);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable page);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime1,
                                                                                LocalDateTime dateTime2,
                                                                                Pageable page);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime dateTime,
                                                                   Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime,
                                                                    Pageable page);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status, Pageable page);

    List<Booking> findAllByItemIdAndStatusOrderByStartDesc(Long itemId, Status status);

    List<Booking> findAllByBookerIdAndItemIdAndEndBeforeAndStatusOrderByStartDesc(Long bookerId, Long itemId,
                                                                                  LocalDateTime dateTime,
                                                                                  Status status);

    List<Booking> findAllByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime dateTime,
                                                                       Status status);

    List<Booking> findAllByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime dateTime,
                                                                       Status status);

    Integer countAllByBookerId(Long userId);
}