package ru.practicum.shareit.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.practicum.shareit.item.model.Item;

import java.io.IOException;

public class ItemSerializer extends JsonSerializer<Item> {
    @Override
    public void serialize(Item item, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", item.getId());
        jsonGenerator.writeStringField("name", item.getName());
        jsonGenerator.writeStringField("description", item.getDescription());
        jsonGenerator.writeBooleanField("available", item.getAvailable());
        if (item.getRequest() != null) {
            jsonGenerator.writeNumberField("requestId", item.getRequest().getId());
        }
        jsonGenerator.writeEndObject();
    }
}