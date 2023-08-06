package ru.practicum.shareit.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.InnerBookingDtoResponse;
import ru.practicum.shareit.serializer.InnerDtoSerializer;
import static org.assertj.core.api.Assertions.*;
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InnerDtoSerializerTest {
    private final ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(InnerBookingDtoResponse.class, new InnerDtoSerializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void innerDtoSerializerTest() throws JsonProcessingException {
        InnerBookingDtoResponse innerBookingDtoResponse = new InnerBookingDtoResponse();
        innerBookingDtoResponse.setId(1L);
        innerBookingDtoResponse.setBookerId(2L);
        innerBookingDtoResponse.setName("name1");

        String str = "{\"id\":1,\"bookerId\":2,\"name\":\"name1\"}";
        String json = objectMapper.writeValueAsString(innerBookingDtoResponse);

        assertThat(json).isEqualTo(str);
    }
}