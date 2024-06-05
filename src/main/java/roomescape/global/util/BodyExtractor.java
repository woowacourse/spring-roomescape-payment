package roomescape.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResourceAccessException;

public class BodyExtractor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> T extractBody(ClientHttpResponse response, Class<T> clazz) {
        try {
            return MAPPER.readValue(response.getBody(), clazz);
        } catch (IOException e) {
            throw new ResourceAccessException("Body를 찾지 못했습니다", e);
        }
    }
}
