package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.i.api.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.EntityUnavailableException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {
    private static User user1;
    private static Booking booking;

    private static BookingDtoResponse bookingDtoResponse;
    private static BookingDtoRequest bookingDtoRequest;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookingService bookingService;

    @MockBean
    BookingDtoMapper bookingDtoMapper;

    @BeforeAll()
    public static void beforeEach() {
        LocalDateTime ndtm = LocalDateTime.now();

        user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@yandex.ru");

        Item item1 = new Item();
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
        booking.setStart(ndtm.plusMinutes(10L));
        booking.setEnd(ndtm.plusHours(1L));
        booking.setItem(item1);
        booking.setBooker(user2);
        booking.setStatus(Status.APPROVED);

        bookingDtoResponse = new BookingDtoResponse();
        bookingDtoResponse.setId(booking.getId());
        bookingDtoResponse.setStart(booking.getStart());
        bookingDtoResponse.setEnd(booking.getEnd());
        bookingDtoResponse.setStatus(booking.getStatus());
        bookingDtoResponse.setBooker(booking.getBooker());
        bookingDtoResponse.setItem(booking.getItem());
        bookingDtoResponse.setName(booking.getItem().getName());

        bookingDtoRequest = new BookingDtoRequest(booking.getStart(), booking.getEnd(), booking.getItem().getId());
    }

    @SneakyThrows
    @Test
    public void createBooking_WhenAllOK_ThenReturnBookingTest() {
        when(bookingDtoMapper.dtoRequestToBooking(any(), any()))
                .thenReturn(booking);

        when(bookingService.createBooking(any()))
                .thenReturn(booking);

        when(bookingDtoMapper.bookingToDtoResponse(any(), any()))
                .thenReturn(bookingDtoResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString().substring(0,
                        booking.getStart().toString().length() - 2))))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString().substring(0,
                        booking.getEnd().toString().length() - 2))))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.name", is(booking.getItem().getName())));
    }

    @SneakyThrows
    @Test
    public void createBooking_WhenItemUnavailable_ThenReturn400ErrorCodeTest() {
        when(bookingDtoMapper.dtoRequestToBooking(any(), any()))
                .thenReturn(booking);

        when(bookingService.createBooking(any()))
                .thenReturn(booking);

        when(bookingDtoMapper.bookingToDtoResponse(any(), any()))
                .thenThrow(new EntityUnavailableException("Item unavailable for booking."));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void updateBooking_WhenAllOK_ThenReturnBookingTest() {
        when(bookingDtoMapper.dtoRequestToBooking(any(), any()))
                .thenReturn(booking);

        when(bookingService.updateBooking(any(), any(), any()))
                .thenReturn(booking);

        when(bookingDtoMapper.bookingToDtoResponse(any(), any()))
                .thenReturn(bookingDtoResponse);

        mvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString().substring(0,
                        booking.getStart().toString().length() - 2))))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString().substring(0,
                        booking.getEnd().toString().length() - 2))))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.name", is(booking.getItem().getName())));
    }

    @SneakyThrows
    @Test
    public void updateBooking_WhenBookingIsAlreadyApproved_ThenReturn400ErrorCodeTest() {
        when(bookingDtoMapper.dtoRequestToBooking(any(), any()))
                .thenReturn(booking);

        when(bookingService.updateBooking(any(), any(), any()))
                .thenReturn(booking);

        when(bookingDtoMapper.bookingToDtoResponse(any(), any()))
                .thenThrow(new EntityUnavailableException("Booking already approved."));

        mvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void getBookingById_WhenAllOK_ThenReturnBookingTest() {
        when(bookingDtoMapper.dtoRequestToBooking(any(), any()))
                .thenReturn(booking);

        when(bookingService.getBookingInfo(any(), any()))
                .thenReturn(booking);

        when(bookingDtoMapper.bookingToDtoResponse(any(), any()))
                .thenReturn(bookingDtoResponse);

        mvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString().substring(0,
                        booking.getStart().toString().length() - 2))))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString().substring(0,
                        booking.getEnd().toString().length() - 2))))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.name", is(booking.getItem().getName())));
    }

    @SneakyThrows
    @Test
    public void getBookingById_WhenBookingDoesNotExist_ThenReturn404ErrorCodeTest() {
        when(bookingDtoMapper.dtoRequestToBooking(any(), any()))
                .thenReturn(booking);

        when(bookingService.getBookingInfo(any(), any()))
                .thenReturn(booking);

        when(bookingDtoMapper.bookingToDtoResponse(any(), any()))
                .thenThrow(new EntityDoesNotExistException("Booking doesn't exist."));

        mvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void getAllBookingsByUser_WhenAllOK_ThenReturnBookingsTest() {
        List<BookingDtoResponse> bookingDtoResponses = new ArrayList<>();
        bookingDtoResponses.add(bookingDtoResponse);

        when(bookingDtoMapper.bookingsToDtosResponse(any(), any()))
                .thenReturn(bookingDtoResponses);

        when(bookingService.getAllBookingsByUser(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        String response = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(mapper.writeValueAsString(bookingDtoResponses)).isEqualTo(response);
    }

    @SneakyThrows
    @Test
    public void getAllBookingsByUser_WhenIncorrectParams_ThenReturn400ErrorCodeTest() {
        when(bookingDtoMapper.bookingsToDtosResponse(any(), any()))
                .thenThrow(new InvalidParameterException("Incorrect parameters."));

        when(bookingService.getAllBookingsByUser(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "25"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void getAllBookingsByOwner_WhenAllOK_ThenReturnBookingsTest() {
        List<BookingDtoResponse> bookingDtoResponses = new ArrayList<>();
        bookingDtoResponses.add(bookingDtoResponse);

        when(bookingDtoMapper.bookingsToDtosResponse(any(), any()))
                .thenReturn(bookingDtoResponses);

        when(bookingService.getAllBookingsByUser(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        String response = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(mapper.writeValueAsString(bookingDtoResponses)).isEqualTo(response);
    }

    @SneakyThrows
    @Test
    public void getAllBookingsByOwner_WhenIncorrectParams_ThenReturn400ErrorCodeTest() {
        when(bookingDtoMapper.bookingsToDtosResponse(any(), any()))
                .thenThrow(new InvalidParameterException("Incorrect parameters."));

        when(bookingService.getAllBookingsByUser(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "25"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void createBooking_WhenEndTimeIsBeforeStartTime_ThenReturn400ErrorCodeTest() {
        bookingDtoRequest.setEnd(LocalDateTime.now());

        when(bookingDtoMapper.dtoRequestToBooking(any(), any()))
                .thenReturn(booking);

        when(bookingService.createBooking(any()))
                .thenReturn(booking);

        when(bookingDtoMapper.bookingToDtoResponse(any(), any()))
                .thenReturn(bookingDtoResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .header("X-Sharer-User-Id", user1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}