package ru.practicum.shareit.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.practicum.shareit.booking.dto.InnerBookingDtoResponse;

import java.io.IOException;

public class InnerDtoSerializer extends JsonSerializer<InnerBookingDtoResponse> {
    @Override
    public void serialize(InnerBookingDtoResponse innerDtoForBookingResponse, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", innerDtoForBookingResponse.getId());
        jsonGenerator.writeNumberField("bookerId", innerDtoForBookingResponse.getBookerId());
        jsonGenerator.writeStringField("name", innerDtoForBookingResponse.getName());
        jsonGenerator.writeEndObject();
    }
}