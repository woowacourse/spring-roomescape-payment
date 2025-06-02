package roomescape.global.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CustomRequestMapper {
    private final ObjectMapper objectMapper;

    public CustomRequestMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> convertMap(Object object) {
        return objectMapper.convertValue(object, Map.class);
    }
}
