package roomescape.converter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;

public class ClientResponseConverter {

    private final ObjectMapper objectMapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    ;

    public ClientResponseConverter() {
    }

    public JsonNode toJsonNode(ClientHttpResponse response) throws IOException {
        return objectMapper.readTree(response.getBody());
    }

    public String toTextFromPath(JsonNode jsonNode, String path) {
        return jsonNode.path(path).asText();
    }
}
