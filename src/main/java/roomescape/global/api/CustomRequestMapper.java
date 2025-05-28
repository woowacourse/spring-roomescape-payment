package roomescape.global.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CustomRequestMapper {
    private final ObjectMapper mapper;

    public CustomRequestMapper(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Map<String, Object> convertMap(Object object) {
        return mapper.convertValue(object, Map.class);
    }
}
