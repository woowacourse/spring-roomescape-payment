package roomescape.global.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import roomescape.global.api.ExternalApiErrorResponse;

@Component
public class CustomResponseMapper {
    private final ObjectMapper objectMapper;

    public CustomResponseMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ExternalApiErrorResponse convertJsonToErrorResponse(
            final String jsonString,
            final Class<? extends ExternalApiErrorResponse> responseType
    ) {
        try {
            return objectMapper.readValue(jsonString, responseType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
