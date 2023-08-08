package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.InnerBookingDtoResponse;
import ru.practicum.shareit.booking.i.api.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.i.api.CommentService;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.i.api.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoMapperTests {
    private final ItemDtoMapper itemDtoMapper;

    private static User user1;
    private static User user2;
    private static Item item1;
    private static Booking lastBooking;
    private static Booking nextBooking;
    private static List<Booking> lastBookings;
    private static List<Booking> nextBookings;
    private static LocalDateTime ndtm;
    private static ItemRequest itemRequest1;

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

        item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description of item1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        item1.setRequest(itemRequest1);

        lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(ndtm.minusMinutes(10L));
        lastBooking.setEnd(ndtm.plusHours(1L));
        lastBooking.setItem(item1);
        lastBooking.setBooker(user2);
        lastBooking.setStatus(Status.APPROVED);

        lastBookings = new ArrayList<>();
        lastBookings.add(lastBooking);

        nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStart(ndtm.plusMinutes(10L));
        nextBooking.setEnd(ndtm.plusHours(1L));
        nextBooking.setItem(item1);
        nextBooking.setBooker(user2);
        nextBooking.setStatus(Status.APPROVED);

        nextBookings = new ArrayList<>();
        nextBookings.add(nextBooking);
    }

    @Test
    public void convertItemToItemDtoResponseTest() {
        BookingService bookingService = Mockito.mock(BookingService.class);
        Mockito
                .when(bookingService.getListForLastBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBookings);
        Mockito
                .when(bookingService.getListForNextBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBookings);

        ReflectionTestUtils.setField(itemDtoMapper, "bookingService", bookingService);

        CommentService commentService = Mockito.mock(CommentService.class);
        Mockito
                .when(commentService.getAllCommentsByItemId(Mockito.any()))
                .thenReturn(new ArrayList<>());

        ReflectionTestUtils.setField(itemDtoMapper, "commentService", commentService);

        ItemDtoResponse itemDtoResponse = itemDtoMapper.convertItemToItemDtoResponse(item1, user1.getId(), ndtm);

        assertThat(itemDtoResponse.getId()).isEqualTo(item1.getId());
        assertThat(itemDtoResponse.getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponse.getDescription()).isEqualTo(item1.getDescription());
        assertThat(itemDtoResponse.getAvailable()).isEqualTo(item1.getAvailable());
        assertThat(itemDtoResponse.getLastBooking().getId()).isEqualTo(lastBooking.getId());
        assertThat(itemDtoResponse.getLastBooking().getBookerId()).isEqualTo(lastBooking.getBooker().getId());
        assertThat(itemDtoResponse.getLastBooking().getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponse.getNextBooking().getId()).isEqualTo(nextBooking.getId());
        assertThat(itemDtoResponse.getNextBooking().getBookerId()).isEqualTo(nextBooking.getBooker().getId());
        assertThat(itemDtoResponse.getNextBooking().getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponse.getRequestId()).isEqualTo(itemRequest1.getId());
    }

    @Test
    public void convertItemToItemDtoResponseWhenBookingsAreEmptyTest() {
        BookingService bookingService = Mockito.mock(BookingService.class);
        Mockito
                .when(bookingService.getListForLastBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());
        Mockito
                .when(bookingService.getListForNextBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        ReflectionTestUtils.setField(itemDtoMapper, "bookingService", bookingService);

        CommentService commentService = Mockito.mock(CommentService.class);
        Mockito
                .when(commentService.getAllCommentsByItemId(Mockito.any()))
                .thenReturn(new ArrayList<>());

        ReflectionTestUtils.setField(itemDtoMapper, "commentService", commentService);

        ItemDtoResponse itemDtoResponse = itemDtoMapper.convertItemToItemDtoResponse(item1, user1.getId(), ndtm);

        assertThat(itemDtoResponse.getId()).isEqualTo(item1.getId());
        assertThat(itemDtoResponse.getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponse.getDescription()).isEqualTo(item1.getDescription());
        assertThat(itemDtoResponse.getAvailable()).isEqualTo(item1.getAvailable());
        assertThat(itemDtoResponse.getLastBooking()).isNull();
        assertThat(itemDtoResponse.getNextBooking()).isNull();
    }

    @Test
    public void convertItemToItemDtoResponseForNonItemOwnerTest() {
        BookingService bookingService = Mockito.mock(BookingService.class);
        Mockito
                .when(bookingService.getListForLastBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBookings);
        Mockito
                .when(bookingService.getListForNextBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBookings);

        ReflectionTestUtils.setField(itemDtoMapper, "bookingService", bookingService);

        CommentService commentService = Mockito.mock(CommentService.class);
        Mockito
                .when(commentService.getAllCommentsByItemId(Mockito.any()))
                .thenReturn(new ArrayList<>());

        ReflectionTestUtils.setField(itemDtoMapper, "commentService", commentService);

        ItemDtoResponse itemDtoResponse = itemDtoMapper.convertItemToItemDtoResponse(item1, user2.getId(), ndtm);

        assertThat(itemDtoResponse.getId()).isEqualTo(item1.getId());
        assertThat(itemDtoResponse.getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponse.getDescription()).isEqualTo(item1.getDescription());
        assertThat(itemDtoResponse.getAvailable()).isEqualTo(item1.getAvailable());
        assertThat(itemDtoResponse.getLastBooking()).isNull();
        assertThat(itemDtoResponse.getNextBooking()).isNull();
    }

    @Test
    public void itemToItemDtoResponseTest() {
        BookingService bookingService = Mockito.mock(BookingService.class);
        Mockito
                .when(bookingService.getListForLastBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBookings);
        Mockito
                .when(bookingService.getListForNextBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBookings);

        ReflectionTestUtils.setField(itemDtoMapper, "bookingService", bookingService);

        ItemDtoResponse itemDtoResponse = itemDtoMapper.itemToItemDtoResponse(item1, user1.getId());

        assertThat(itemDtoResponse.getId()).isEqualTo(item1.getId());
        assertThat(itemDtoResponse.getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponse.getDescription()).isEqualTo(item1.getDescription());
        assertThat(itemDtoResponse.getAvailable()).isEqualTo(item1.getAvailable());
        assertThat(itemDtoResponse.getLastBooking().getId()).isEqualTo(lastBooking.getId());
        assertThat(itemDtoResponse.getLastBooking().getBookerId()).isEqualTo(lastBooking.getBooker().getId());
        assertThat(itemDtoResponse.getLastBooking().getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponse.getNextBooking().getId()).isEqualTo(nextBooking.getId());
        assertThat(itemDtoResponse.getNextBooking().getBookerId()).isEqualTo(nextBooking.getBooker().getId());
        assertThat(itemDtoResponse.getNextBooking().getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponse.getRequestId()).isEqualTo(itemRequest1.getId());
    }

    @Test
    public void itemDtoRequestToItemTest() {
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest();
        itemDtoRequest.setId(1L);
        itemDtoRequest.setName("name1");
        itemDtoRequest.setDescription("description1");
        itemDtoRequest.setAvailable(true);
        itemDtoRequest.setRequestId(1L);

        ItemRequestService itemRequestService = Mockito.mock(ItemRequestService.class);
        Mockito
                .when(itemRequestService.getItemRequestById(Mockito.any()))
                .thenReturn(itemRequest1);

        ReflectionTestUtils.setField(itemDtoMapper, "itemRequestService", itemRequestService);

        Item item = itemDtoMapper.itemDtoRequestToItem(itemDtoRequest);

        assertThat(item.getId()).isEqualTo(itemDtoRequest.getId());
        assertThat(item.getName()).isEqualTo(itemDtoRequest.getName());
        assertThat(item.getDescription()).isEqualTo(itemDtoRequest.getDescription());
        assertThat(item.getAvailable()).isEqualTo(itemDtoRequest.getAvailable());
        assertThat(item.getRequest().getId()).isEqualTo(itemDtoRequest.getRequestId());
    }

    @Test
    public void itemsToDtosTest() {
        List<Item> items = new ArrayList<>();
        items.add(item1);

        BookingService bookingService = Mockito.mock(BookingService.class);
        Mockito
                .when(bookingService.getListForLastBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBookings);
        Mockito
                .when(bookingService.getListForNextBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBookings);

        ReflectionTestUtils.setField(itemDtoMapper, "bookingService", bookingService);

        CommentService commentService = Mockito.mock(CommentService.class);
        Mockito
                .when(commentService.getAllCommentsByItemId(Mockito.any()))
                .thenReturn(new ArrayList<>());

        ReflectionTestUtils.setField(itemDtoMapper, "commentService", commentService);

        List<ItemDtoResponse> itemDtoResponses = itemDtoMapper.itemsToDtos(items, user1.getId());

        assertThat(itemDtoResponses.size()).isEqualTo(1);
        assertThat(itemDtoResponses.get(0).getId()).isEqualTo(item1.getId());
        assertThat(itemDtoResponses.get(0).getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponses.get(0).getDescription()).isEqualTo(item1.getDescription());
        assertThat(itemDtoResponses.get(0).getAvailable()).isEqualTo(item1.getAvailable());
        assertThat(itemDtoResponses.get(0).getLastBooking().getId()).isEqualTo(lastBooking.getId());
        assertThat(itemDtoResponses.get(0).getLastBooking().getBookerId()).isEqualTo(lastBooking.getBooker().getId());
        assertThat(itemDtoResponses.get(0).getLastBooking().getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponses.get(0).getNextBooking().getId()).isEqualTo(nextBooking.getId());
        assertThat(itemDtoResponses.get(0).getNextBooking().getBookerId()).isEqualTo(nextBooking.getBooker().getId());
        assertThat(itemDtoResponses.get(0).getNextBooking().getName()).isEqualTo(item1.getName());
        assertThat(itemDtoResponses.get(0).getRequestId()).isEqualTo(itemRequest1.getId());
    }

    @Test
    public void createLastTest() {
        BookingService bookingService = Mockito.mock(BookingService.class);
        Mockito
                .when(bookingService.getListForLastBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBookings);
        ReflectionTestUtils.setField(itemDtoMapper, "bookingService", bookingService);

        InnerBookingDtoResponse innerBookingDtoResponse = ItemDtoMapper.ShortDtoResponseByItemId
                .createLast(item1.getId(), bookingService, ndtm);

        assert innerBookingDtoResponse != null;
        assertThat(innerBookingDtoResponse.getId()).isEqualTo(lastBookings.get(0).getId());
        assertThat(innerBookingDtoResponse.getName()).isEqualTo(lastBookings.get(0).getItem().getName());
        assertThat(innerBookingDtoResponse.getBookerId()).isEqualTo(lastBookings.get(0).getBooker().getId());
    }

    @Test
    public void createNextTest() {
        BookingService bookingService = Mockito.mock(BookingService.class);
        Mockito
                .when(bookingService.getListForNextBookingFinding(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBookings);
        ReflectionTestUtils.setField(itemDtoMapper, "bookingService", bookingService);

        InnerBookingDtoResponse innerBookingDtoResponse = ItemDtoMapper.ShortDtoResponseByItemId
                .createNext(item1.getId(), bookingService, ndtm);

        assert innerBookingDtoResponse != null;
        assertThat(innerBookingDtoResponse.getId()).isEqualTo(nextBookings.get(0).getId());
        assertThat(innerBookingDtoResponse.getName()).isEqualTo(nextBookings.get(0).getItem().getName());
        assertThat(innerBookingDtoResponse.getBookerId()).isEqualTo(nextBookings.get(0).getBooker().getId());
    }
}