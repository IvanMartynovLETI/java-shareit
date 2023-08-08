package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.i.api.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BookingServiceIntegrationTests {
    private final BookingService bookingService;
    private User user1;
    private User user3;
    private Booking booking1;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    public void beforeEach() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@yandex.ru");

        user3 = new User();
        user3.setId(3L);
        user3.setName("user3");
        user3.setEmail("user3@yandex.ru");

        Item item1 = new Item();
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
        booking1.setStart(LocalDateTime.parse("2022-08-05 18:36", FORMATTER));
        booking1.setEnd(LocalDateTime.parse("2022-08-05 19:00", FORMATTER));
        booking1.setItem(item1);
        booking1.setBooker(user2);
        booking1.setStatus(Status.APPROVED);
    }

    @Test
    public void getAllBookingsByOwnerThenReturnBookingsTest() {
        List<Booking> bookings = bookingService.getAllBookingsByOwner(user1.getId(), "ALL", 0, 12);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getStart()).isEqualTo(booking1.getStart());
        assertThat(bookings.get(0).getEnd()).isEqualTo(booking1.getEnd());
        assertThat(bookings.get(0).getStatus()).isEqualTo(booking1.getStatus());
    }

    @Test
    public void createBookingWhenUserNotExistThenThrowEntityDoesNotExistExceptionTest() {
        booking1.setBooker(user3);

        assertThrows(EntityDoesNotExistException.class, () ->
                bookingService.createBooking(booking1));
    }
}