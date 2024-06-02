package roomescape.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResourceAccessException;
import roomescape.global.exception.ErrorResponse;

public class BodyExtractor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ErrorResponse extract(ClientHttpResponse response) {
        try {
            return MAPPER.readValue(response.getBody(), ErrorResponse.class);
        } catch (IOException e) {
            throw new ResourceAccessException("에러 정보를 찾지 못했습니다");
        }
    }

    public static String getMessage(ClientHttpResponse response) {
        return extract(response).message();
    }
}
