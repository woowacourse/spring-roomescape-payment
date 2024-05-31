package roomescape.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResponseBodyExtractor {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String extract(InputStream inputStream, String key) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> properties = mapper.readValue(body, new TypeReference<>() {});
        return properties.get(key).toString();
    }
}
