package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.i.api.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingMapperTests {
    private final BookingDtoMapper bookingDtoMapper;
    private static User user1;
    private static User user3;
    private static Item item1;
    private static Booking booking;

    @BeforeAll
    public static void beforeAll() {
        LocalDateTime ndtm = LocalDateTime.now();

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

        item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description of item1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        item1.setRequest(itemRequest1);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(ndtm.minusMinutes(10L));
        booking.setEnd(ndtm.plusHours(1L));
        booking.setItem(item1);
        booking.setBooker(user2);
        booking.setStatus(Status.APPROVED);
    }

    @Test
    public void bookingToDtoResponseTest() {
        BookingDtoResponse bookingDtoResponse = bookingDtoMapper.bookingToDtoResponse(booking, user1.getId());

        assertThat(bookingDtoResponse.getId()).isEqualTo(booking.getId());
        assertThat(bookingDtoResponse.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDtoResponse.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDtoResponse.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDtoResponse.getBooker()).isEqualTo(booking.getBooker());
        assertThat(bookingDtoResponse.getItem()).isEqualTo(booking.getItem());
        assertThat(bookingDtoResponse.getName()).isEqualTo(booking.getItem().getName());
    }

    @Test
    public void bookingToDtoResponseForAnotherUserTest() {
        BookingDtoResponse bookingDtoResponse = bookingDtoMapper.bookingToDtoResponse(booking, user3.getId());

        assertThat(bookingDtoResponse.getId()).isEqualTo(booking.getId());
        assertThat(bookingDtoResponse.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDtoResponse.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDtoResponse.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDtoResponse.getBooker()).isNull();
        assertThat(bookingDtoResponse.getItem()).isNull();
    }

    @Test
    public void dtoRequestToBookingTest() {
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(booking.getStart(), booking.getEnd(),
                booking.getItem().getId());

        UserService userService = Mockito.mock(UserService.class);
        Mockito
                .when(userService.getUserById(Mockito.any()))
                .thenReturn(user1);

        ReflectionTestUtils.setField(bookingDtoMapper, "userService", userService);

        ItemService itemService = Mockito.mock(ItemService.class);
        Mockito
                .when(itemService.getItemById(Mockito.any()))
                .thenReturn(item1);

        ReflectionTestUtils.setField(bookingDtoMapper, "itemService", itemService);

        Booking booking = bookingDtoMapper.dtoRequestToBooking(bookingDtoRequest, user1.getId());

        assertThat(booking.getStart()).isEqualTo(bookingDtoRequest.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDtoRequest.getEnd());
        assertThat(booking.getItem().getId()).isEqualTo(bookingDtoRequest.getItemId());
    }

    @Test
    public void bookingsToDtosResponseTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        List<BookingDtoResponse> bookingDtoResponses = bookingDtoMapper.bookingsToDtosResponse(bookings,
                user1.getId());

        assertThat(bookingDtoResponses.size()).isEqualTo(bookings.size());
        assertThat(bookingDtoResponses.get(0).getId()).isEqualTo(bookings.get(0).getId());
        assertThat(bookingDtoResponses.get(0).getStart()).isEqualTo(bookings.get(0).getStart());
        assertThat(bookingDtoResponses.get(0).getEnd()).isEqualTo(bookings.get(0).getEnd());
        assertThat(bookingDtoResponses.get(0).getStatus()).isEqualTo(bookings.get(0).getStatus());
        assertThat(bookingDtoResponses.get(0).getBooker()).isEqualTo(bookings.get(0).getBooker());
        assertThat(bookingDtoResponses.get(0).getItem()).isEqualTo(bookings.get(0).getItem());
        assertThat(bookingDtoResponses.get(0).getName()).isEqualTo(bookings.get(0).getItem().getName());
    }
}