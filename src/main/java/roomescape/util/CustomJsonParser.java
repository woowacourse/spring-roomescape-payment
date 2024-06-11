package roomescape.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.exception.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CustomJsonParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String parseResponse(ClientHttpResponse response, String key) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody()));
            return objectMapper.readTree(bufferedReader)
                    .findValue(key)
                    .asText();
        } catch (IOException e) {
            throw new JsonParseException(e.getMessage());
        }
    }
}
