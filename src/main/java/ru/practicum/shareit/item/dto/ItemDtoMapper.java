package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.InnerBookingDtoResponse;
import ru.practicum.shareit.booking.i.api.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.comment.i.api.CommentService;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemDtoMapper {
    private final BookingService bookingService;
    private final CommentService commentService;
    private final CommentDtoMapper commentDtoMapper;

    public static class ShortDtoResponseByItemId {
        public static InnerBookingDtoResponse createLast(Long itemId, BookingService bService, LocalDateTime dtm) {
            List<Booking> bookings = bService.getListForLastBookingFinding(itemId, dtm, Status.APPROVED);
            if (itemId == null || bookings.isEmpty()) {
                return null;
            } else {
                Booking lastBooking = bookings.get(0);
                return new InnerBookingDtoResponse(lastBooking.getId(), lastBooking.getBooker().getId(),
                        lastBooking.getItem().getName());
            }
        }

        public static InnerBookingDtoResponse createNext(Long itemId, BookingService bService, LocalDateTime dtm) {
            List<Booking> bookings = bService.getListForNextBookingFinding(itemId, dtm, Status.APPROVED);

            if (itemId == null || bookings.isEmpty()) {
                return null;
            } else {
                Booking nextBooking = bookings.get(0);
                return new InnerBookingDtoResponse(nextBooking.getId(), nextBooking.getBooker().getId(),
                        nextBooking.getItem().getName());
            }
        }
    }

    public Optional<ItemDtoResponse> itemToItemDtoResponse(Item item, Long userId, LocalDateTime ndtm) {
        if (item == null) {
            return Optional.empty();
        } else {
            return Optional.of(convertItemToItemDtoResponse(item, userId, ndtm));
        }
    }

    public Item itemDtoRequestToItem(ItemDtoRequest itemDtoRequest) {
        Item item = new Item();
        item.setId(itemDtoRequest.getId());
        item.setName(itemDtoRequest.getName());
        item.setDescription(itemDtoRequest.getDescription());
        item.setAvailable(itemDtoRequest.getAvailable());

        return item;
    }

    public Optional<List<ItemDtoResponse>> itemsToDtos(List<Item> items, Long userId, LocalDateTime ndtm) {
        if (items == null) {
            return Optional.empty();
        } else {
            return Optional.of(items
                    .stream()
                    .map((Item i) -> convertItemToItemDtoResponse(i, userId, ndtm))
                    .collect(Collectors.toList()));
        }
    }

    public ItemDtoResponse convertItemToItemDtoResponse(Item item, Long userId, LocalDateTime ndtm) {
        ItemDtoResponse itemDtoResponse = new ItemDtoResponse();
        itemDtoResponse.setId(item.getId());
        itemDtoResponse.setName(item.getName());
        itemDtoResponse.setDescription(item.getDescription());
        itemDtoResponse.setAvailable(item.getAvailable());

        if (item.getOwner().getId().equals(userId)) {
            itemDtoResponse.setLastBooking(ShortDtoResponseByItemId.createLast(item.getId(), bookingService, ndtm));
            itemDtoResponse.setNextBooking(ShortDtoResponseByItemId.createNext(item.getId(), bookingService, ndtm));
        } else {
            itemDtoResponse.setLastBooking(null);
            itemDtoResponse.setNextBooking(null);
        }

        itemDtoResponse.setComments(commentDtoMapper.commentsToDtos(commentService
                .getAllCommentsByItemId(item.getId())));

        return itemDtoResponse;
    }
}