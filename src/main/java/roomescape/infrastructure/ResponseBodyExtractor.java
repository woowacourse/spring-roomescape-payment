package roomescape.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roomescape.exception.RoomEscapeBusinessException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResponseBodyExtractor {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseBodyExtractor.class);

    public static String extract(byte[] rawBody, String key) {
        try {
            String body = new String(rawBody, StandardCharsets.UTF_8);
            Map<String, Object> properties = MAPPER.readValue(body, new TypeReference<>() {});
            Object value = properties.get(key);
            if (value == null) {
                return "";
            }
            return value.toString();
        } catch (IOException e) {
            LOGGER.error("response body extractor", e);
            throw new RoomEscapeBusinessException("JSON 객체로 파싱할 수 없습니다.");
        }
    }
}
