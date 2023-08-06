package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.i.api.BookingRepository;
import ru.practicum.shareit.booking.i.impl.BookingServiceImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.EntityUnavailableException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {
    private static User user1;
    private static User user2;
    private static User user3;
    private static Item item1;
    private static Booking booking1;
    private static LocalDateTime ndtm;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserService userService;


    @BeforeAll
    public static void beforeAll() {
        ndtm = LocalDateTime.now();

        user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@yandex.ru");

        user3 = new User();
        user3.setId(3L);
        user3.setName("user3");
        user3.setEmail("user3@yandex.ru");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description of item1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        item1.setRequest(itemRequest1);

        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(ndtm.minusMinutes(10L));
        booking1.setEnd(ndtm.plusHours(1L));
        booking1.setItem(item1);
        booking1.setBooker(user2);
        booking1.setStatus(Status.APPROVED);
    }

    @Test
    public void createBooking_WhenAllOK_ThenReturnBookingTest() {

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.findAllByItemIdAndStatusOrderByStartDesc(any(), any()))
                .thenReturn(Collections.emptyList());

        when(bookingRepository.save(any()))
                .thenReturn(booking1);

        assertThat(bookingService.createBooking(booking1)).isEqualTo(booking1);
    }

    @Test
    public void createBooking_WhenItemUnavailableForBooking_ThenThrowEntityUnavailableExceptionTest() {
        item1.setAvailable(false);
        booking1.setItem(item1);

        assertThrows(EntityUnavailableException.class, () -> bookingService.createBooking(booking1));

        item1.setAvailable(true);
        booking1.setItem(item1);
    }

    @Test
    public void createBooking_WhenUserNotExist_ThenThrowEntityDoesNotExistExceptionTest() {
        when(userService.getUserById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> bookingService.createBooking(booking1));
    }

    @Test
    public void createBooking_WhenBookerIsAnItemOwner_ThenThrowEntityDoesNotExistExceptionTest() {
        when(userService.getUserById(any()))
                .thenReturn(user1);

        booking1.setBooker(user1);

        assertThrows(EntityDoesNotExistException.class, () -> bookingService.createBooking(booking1));

        booking1.setBooker(user2);
    }

    @Test
    public void createBooking_WhenBookingAlreadyApproved_ThenThrowEntityDoesNotExistExceptionTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.findAllByItemIdAndStatusOrderByStartDesc(any(), any()))
                .thenReturn(bookings);

        assertThrows(EntityDoesNotExistException.class, () -> bookingService.createBooking(booking1));
    }

    @Test
    public void updateBooking_WhenAllOKAndBookingApproved_ThenReturnBookingTest() {
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(ndtm.minusMinutes(10L));
        booking2.setEnd(ndtm.plusHours(1L));
        booking2.setItem(item1);
        booking2.setBooker(user2);
        booking2.setStatus(Status.WAITING);

        when(bookingRepository.findBookingById(2L))
                .thenReturn(booking2);

        when(bookingRepository.save(any()))
                .thenReturn(booking2);

        assertThat(bookingService.updateBooking(booking2.getId(), user1.getId(), true)).isEqualTo(booking2);
    }

    @Test
    public void updateBooking_WhenAllOKAndBookingRejected_ThenReturnBookingTest() {
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(ndtm.minusMinutes(10L));
        booking2.setEnd(ndtm.plusHours(1L));
        booking2.setItem(item1);
        booking2.setBooker(user2);
        booking2.setStatus(Status.WAITING);

        when(bookingRepository.findBookingById(2L))
                .thenReturn(booking2);

        when(bookingRepository.save(any()))
                .thenReturn(booking2);

        assertThat(bookingService.updateBooking(booking2.getId(), user1.getId(), false).getStatus())
                .isEqualTo(Status.REJECTED);
    }

    @Test
    public void updateBooking_WhenUserIsNotAnOwnerOfItem_ThenThrowEntityDoesNotExistExceptionTest() {
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(ndtm.minusMinutes(10L));
        booking2.setEnd(ndtm.plusHours(1L));
        booking2.setItem(item1);
        booking2.setBooker(user2);
        booking2.setStatus(Status.WAITING);

        when(bookingRepository.findBookingById(2L))
                .thenReturn(booking2);

        assertThrows(EntityDoesNotExistException.class, () -> bookingService.updateBooking(booking2.getId(),
                user2.getId(), false));
    }

    @Test
    public void updateBooking_WhenBookingAlreadyApproved_ThenThrowEntityUnavailableExceptionTest() {
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(ndtm.minusMinutes(10L));
        booking2.setEnd(ndtm.plusHours(1L));
        booking2.setItem(item1);
        booking2.setBooker(user2);
        booking2.setStatus(Status.APPROVED);

        when(bookingRepository.findBookingById(2L))
                .thenReturn(booking2);

        assertThrows(EntityUnavailableException.class, () -> bookingService.updateBooking(booking2.getId(),
                user1.getId(), false));
    }

    @Test
    public void getBookingInfo_WhenAllOK_ThenReturnBookingTest() {
        when(bookingRepository.findBookingById(any()))
                .thenReturn(booking1);

        assertThat(bookingService.getBookingInfo(booking1.getId(), user1.getId())).isEqualTo(booking1);
    }

    @Test
    public void getBookingInfo_WhenBookingNotExist_ThenThrowEntityDoesNotExistExceptionTest() {
        when(bookingRepository.findBookingById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> bookingService.getBookingInfo(booking1.getId(),
                user1.getId()));
    }

    @Test
    public void getBookingInfo_WhenNoSuchEntity_ThenThrowEntityDoesNotExistExceptionTest() {
        when(bookingRepository.findBookingById(any()))
                .thenReturn(booking1);

        assertThrows(EntityDoesNotExistException.class, () -> bookingService.getBookingInfo(booking1.getId(),
                user3.getId()));
    }

    @Test
    public void getAllBookingsByUser_WhenIncorrectParams_ThenThrowInvalidParameterExceptionTest() {
        int from = -1;
        int size = 1;

        assertThrows(InvalidParameterException.class, () -> bookingService.getAllBookingsByUser(user1.getId(),
                "ALL", from, size));
    }

    @Test
    public void getAllBookingsByUser_WhenUserNotExist_ThenThrowEntityDoesNotExistExceptionTest() {
        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> bookingService.getAllBookingsByUser(user1.getId(),
                "ALL", from, size));
    }

    @Test
    public void getAllBookingsByUser_WhenAll_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.countAllByBookerId(any()))
                .thenReturn(1);

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByUser(user2.getId(), "ALL", from, size)).isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByUser_WhenCurrent_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.countAllByBookerId(any()))
                .thenReturn(1);

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByUser(user2.getId(), "CURRENT", from, size)).isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByUser_WhenPast_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.countAllByBookerId(any()))
                .thenReturn(1);

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByUser(user2.getId(), "PAST", from, size)).isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByUser_WhenFuture_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.countAllByBookerId(any()))
                .thenReturn(1);

        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByUser(user2.getId(), "FUTURE", from, size)).isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByUser_WhenWaiting_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.countAllByBookerId(any()))
                .thenReturn(1);

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByUser(user2.getId(), "WAITING", from, size)).isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByUser_WhenRejected_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.countAllByBookerId(any()))
                .thenReturn(1);

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByUser(user2.getId(), "REJECTED", from, size))
                .isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByUser_WhenUnknown_ThenThrowInvalidParameterExceptionTest() {
        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.countAllByBookerId(any()))
                .thenReturn(1);

        assertThrows(InvalidParameterException.class, () ->
                bookingService.getAllBookingsByUser(user2.getId(), "UNKNOWN", from, size));
    }

    @Test
    public void getAllBookingsByOwner_WhenIncorrectParams_ThenThrowInvalidParameterExceptionTest() {
        int from = -1;
        int size = 1;

        assertThrows(InvalidParameterException.class, () -> bookingService.getAllBookingsByOwner(user1.getId(),
                "ALL", from, size));
    }

    @Test
    public void getAllBookingsByOwner_WhenOwnerNotExist_ThenThrowEntityDoesNotExistExceptionTest() {
        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> bookingService.getAllBookingsByOwner(user1.getId(),
                "ALL", from, size));
    }

    @Test
    public void getAllBookingsByOwner_WhenAll_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByOwner(user2.getId(), "ALL", from, size)).isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByOwner_WhenCurrent_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(),
                any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByOwner(user2.getId(), "CURRENT", from, size))
                .isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByOwner_WhenPast_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByOwner(user2.getId(), "PAST", from, size)).isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByOwner_WhenFuture_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByOwner(user2.getId(), "FUTURE", from, size)).isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByOwner_WhenWaiting_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByOwner(user2.getId(), "WAITING", from, size))
                .isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByOwner_WhenRejected_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getAllBookingsByOwner(user2.getId(), "REJECTED", from, size))
                .isEqualTo(bookings);
    }

    @Test
    public void getAllBookingsByOwner_WhenUnknown_ThenReturnInvalidParameterExceptionTest() {
        int from = 0;
        int size = 1;

        when(userService.getUserById(any()))
                .thenReturn(user1);

        assertThrows(InvalidParameterException.class, () ->
                bookingService.getAllBookingsByOwner(user2.getId(), "UNKNOWN", from, size));
    }

    @Test
    public void getListForLastBookingFinding_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        when(bookingRepository.findAllByItemIdAndStartBeforeAndStatusOrderByEndDesc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getListForLastBookingFinding(item1.getId(), ndtm, Status.APPROVED))
                .isEqualTo(bookings);
    }

    @Test
    public void getListForLastBookingFinding_WhenEmptyBookings_ThenReturnEmptyListTest() {
        List<Booking> bookings = new ArrayList<>();

        when(bookingRepository.findAllByItemIdAndStartBeforeAndStatusOrderByEndDesc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getListForLastBookingFinding(item1.getId(), ndtm, Status.APPROVED))
                .isEmpty();
    }

    @Test
    public void getListForNextBookingFinding_ThenReturnBookingsTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        when(bookingRepository.findAllByItemIdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getListForNextBookingFinding(item1.getId(), ndtm, Status.APPROVED))
                .isEqualTo(bookings);
    }

    @Test
    public void getListForNextBookingFinding_WhenEmptyBookings_ThenReturnEmptyListTest() {
        List<Booking> bookings = new ArrayList<>();

        when(bookingRepository.findAllByItemIdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookingService.getListForNextBookingFinding(item1.getId(), ndtm, Status.APPROVED))
                .isEmpty();
    }

    @Test
    public void validateCommentAuthor_WhenValid_ThenReturnTrueTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusOrderByStartDesc(any(), any(), any(),
                any()))
                .thenReturn(bookings);

        assertTrue(bookingService.validateCommentAuthor(user1.getId(), item1.getId()));
    }

    @Test
    public void validateCommentAuthor_WhenInvalid_ThenReturnFalseTest() {
        List<Booking> bookings = new ArrayList<>();

        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusOrderByStartDesc(any(), any(), any(),
                any()))
                .thenReturn(bookings);

        assertFalse(bookingService.validateCommentAuthor(user1.getId(), item1.getId()));
    }

    @Test
    public void validateBooking_WhenOK_ThenReturnTrueTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        List<Booking> bookings = new ArrayList<>();

        Booking booking2 = new Booking();
        booking2.setStart(booking1.getEnd().plusMinutes(1L));
        booking2.setEnd(booking2.getStart().plusHours(1L));
        booking2.setId(2L);
        booking2.setItem(item1);
        booking2.setStatus(Status.APPROVED);
        booking2.setBooker(booking1.getBooker());

        Booking booking3 = new Booking();
        booking3.setStart(booking2.getEnd().plusMinutes(1L));
        booking3.setEnd(booking3.getStart().plusHours(1L));
        booking3.setId(3L);
        booking3.setItem(item1);
        booking3.setStatus(Status.APPROVED);
        booking3.setBooker(booking2.getBooker());

        Booking booking4 = new Booking();
        booking4.setStart(booking3.getEnd().plusMinutes(1L));
        booking4.setEnd(booking4.getStart().plusHours(1L));
        booking4.setId(4L);
        booking4.setItem(item1);
        booking4.setStatus(Status.APPROVED);
        booking4.setBooker(booking3.getBooker());

        bookings.add(booking3);
        bookings.add(booking2);
        bookings.add(booking1);

        Method bookingValidator = BookingServiceImpl.class
                .getDeclaredMethod("bookingValidator", List.class, Booking.class);
        bookingValidator.setAccessible(true);

        assertTrue((Boolean) bookingValidator.invoke(bookingService, bookings, booking4));
    }

    @Test
    public void validateBooking_WhenBookingIntersectsWithBooking3_ThenReturnFalseTest() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        List<Booking> bookings = new ArrayList<>();

        Booking booking2 = new Booking();
        booking2.setStart(booking1.getEnd().plusMinutes(1L));
        booking2.setEnd(booking2.getStart().plusHours(1L));
        booking2.setId(2L);
        booking2.setItem(item1);
        booking2.setStatus(Status.APPROVED);
        booking2.setBooker(booking1.getBooker());

        Booking booking3 = new Booking();
        booking3.setStart(booking2.getEnd().plusMinutes(1L));
        booking3.setEnd(booking3.getStart().plusHours(1L));
        booking3.setId(3L);
        booking3.setItem(item1);
        booking3.setStatus(Status.APPROVED);
        booking3.setBooker(booking2.getBooker());

        Booking booking4 = new Booking();
        booking4.setStart(booking3.getEnd());
        booking4.setEnd(booking4.getStart().plusHours(1L));
        booking4.setId(4L);
        booking4.setItem(item1);
        booking4.setStatus(Status.APPROVED);
        booking4.setBooker(booking3.getBooker());

        bookings.add(booking3);
        bookings.add(booking2);
        bookings.add(booking1);

        Method bookingValidator = BookingServiceImpl.class
                .getDeclaredMethod("bookingValidator", List.class, Booking.class);
        bookingValidator.setAccessible(true);

        assertFalse((Boolean) bookingValidator.invoke(bookingService, bookings, booking4));
    }

    @Test
    public void validateBooking_WhenBookingIntersectsWithBooking1_ThenReturnFalseTest() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        List<Booking> bookings = new ArrayList<>();

        Booking booking2 = new Booking();
        booking2.setStart(booking1.getEnd().plusMinutes(1L));
        booking2.setEnd(booking2.getStart().plusHours(1L));
        booking2.setId(2L);
        booking2.setItem(item1);
        booking2.setStatus(Status.APPROVED);
        booking2.setBooker(booking1.getBooker());

        Booking booking3 = new Booking();
        booking3.setStart(booking2.getEnd().plusMinutes(1L));
        booking3.setEnd(booking3.getStart().plusHours(1L));
        booking3.setId(3L);
        booking3.setItem(item1);
        booking3.setStatus(Status.APPROVED);
        booking3.setBooker(booking2.getBooker());

        Booking booking4 = new Booking();
        booking4.setEnd(booking3.getStart());
        booking4.setStart(booking4.getEnd().minusHours(1L));
        booking4.setId(4L);
        booking4.setItem(item1);
        booking4.setStatus(Status.APPROVED);
        booking4.setBooker(booking3.getBooker());

        bookings.add(booking3);
        bookings.add(booking2);
        bookings.add(booking1);

        Method bookingValidator = BookingServiceImpl.class
                .getDeclaredMethod("bookingValidator", List.class, Booking.class);
        bookingValidator.setAccessible(true);

        assertFalse((Boolean) bookingValidator.invoke(bookingService, bookings, booking4));
    }

    @Test
    public void validateBooking_WhenBookingOKBetweenBookings1And2_ThenReturnTrueTest() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        List<Booking> bookings = new ArrayList<>();

        Booking booking2 = new Booking();
        booking2.setStart(booking1.getEnd().plusHours(1L));
        booking2.setEnd(booking2.getStart().plusHours(1L));
        booking2.setId(2L);
        booking2.setItem(item1);
        booking2.setStatus(Status.APPROVED);
        booking2.setBooker(booking1.getBooker());

        Booking booking3 = new Booking();
        booking3.setStart(booking2.getEnd().plusMinutes(1L));
        booking3.setEnd(booking3.getStart().plusHours(1L));
        booking3.setId(3L);
        booking3.setItem(item1);
        booking3.setStatus(Status.APPROVED);
        booking3.setBooker(booking2.getBooker());

        Booking booking4 = new Booking();
        booking4.setStart(booking1.getEnd().plusMinutes(10L));
        booking4.setEnd(booking4.getStart().plusMinutes(30L));
        booking4.setId(4L);
        booking4.setItem(item1);
        booking4.setStatus(Status.APPROVED);
        booking4.setBooker(booking3.getBooker());

        bookings.add(booking3);
        bookings.add(booking2);
        bookings.add(booking1);

        Method bookingValidator = BookingServiceImpl.class
                .getDeclaredMethod("bookingValidator", List.class, Booking.class);
        bookingValidator.setAccessible(true);

        assertTrue((Boolean) bookingValidator.invoke(bookingService, bookings, booking4));
    }

    @Test
    public void validateBooking_WhenBookingOKBetweenBookings2And3_ThenReturnTrueTest() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        List<Booking> bookings = new ArrayList<>();

        Booking booking2 = new Booking();
        booking2.setStart(booking1.getEnd().plusMinutes(1L));
        booking2.setEnd(booking2.getStart().plusHours(1L));
        booking2.setId(2L);
        booking2.setItem(item1);
        booking2.setStatus(Status.APPROVED);
        booking2.setBooker(booking1.getBooker());

        Booking booking3 = new Booking();
        booking3.setStart(booking2.getEnd().plusHours(1L));
        booking3.setEnd(booking3.getStart().plusHours(1L));
        booking3.setId(3L);
        booking3.setItem(item1);
        booking3.setStatus(Status.APPROVED);
        booking3.setBooker(booking2.getBooker());

        Booking booking4 = new Booking();
        booking4.setStart(booking2.getEnd().plusMinutes(10L));
        booking4.setEnd(booking4.getStart().plusMinutes(30L));
        booking4.setId(4L);
        booking4.setItem(item1);
        booking4.setStatus(Status.APPROVED);
        booking4.setBooker(booking3.getBooker());

        bookings.add(booking3);
        bookings.add(booking2);
        bookings.add(booking1);

        Method bookingValidator = BookingServiceImpl.class
                .getDeclaredMethod("bookingValidator", List.class, Booking.class);
        bookingValidator.setAccessible(true);

        assertTrue((Boolean) bookingValidator.invoke(bookingService, bookings, booking4));
    }

    @Test
    public void validateBooking_WhenBookingOKBeforeBooking1_ThenReturnTrueTest() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        List<Booking> bookings = new ArrayList<>();

        Booking booking2 = new Booking();
        booking2.setStart(booking1.getEnd().plusMinutes(1L));
        booking2.setEnd(booking2.getStart().plusHours(1L));
        booking2.setId(2L);
        booking2.setItem(item1);
        booking2.setStatus(Status.APPROVED);
        booking2.setBooker(booking1.getBooker());

        Booking booking3 = new Booking();
        booking3.setStart(booking2.getEnd().plusMinutes(1L));
        booking3.setEnd(booking3.getStart().plusHours(1L));
        booking3.setId(3L);
        booking3.setItem(item1);
        booking3.setStatus(Status.APPROVED);
        booking3.setBooker(booking2.getBooker());

        Booking booking4 = new Booking();
        booking4.setEnd(booking1.getStart().minusMinutes(1L));
        booking4.setStart(booking4.getEnd().minusMinutes(30L));

        booking4.setId(4L);
        booking4.setItem(item1);
        booking4.setStatus(Status.APPROVED);
        booking4.setBooker(booking3.getBooker());

        bookings.add(booking3);
        bookings.add(booking2);
        bookings.add(booking1);

        Method bookingValidator = BookingServiceImpl.class
                .getDeclaredMethod("bookingValidator", List.class, Booking.class);
        bookingValidator.setAccessible(true);

        assertTrue((Boolean) bookingValidator.invoke(bookingService, bookings, booking4));
    }
}