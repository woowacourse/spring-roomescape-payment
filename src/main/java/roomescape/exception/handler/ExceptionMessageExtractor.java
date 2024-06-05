package roomescape.exception.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ExceptionMessageExtractor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String extractMessage(ClientHttpResponse response) throws IOException {
        String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        JsonNode rootNode = objectMapper.readTree(responseBody);

        return rootNode.path("message").asText();
    }
}
