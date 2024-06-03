package roomescape.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResponseBodyExtractor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String extract(byte[] inputBytes, String key) throws IOException {
        String body = new String(inputBytes, StandardCharsets.UTF_8);
        Map<String, Object> properties = MAPPER.readValue(body, new TypeReference<>() {});
        Object value = properties.get(key);
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
