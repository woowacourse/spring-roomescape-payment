package roomescape.global.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import roomescape.global.dto.TossApiErrorResponse;

@Component
public class JsonStringToObject {
    private final ObjectMapper objectMapper;

    public JsonStringToObject(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TossApiErrorResponse convertTossApiErrorResponse(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, TossApiErrorResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
