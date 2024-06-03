package roomescape.exception.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.PaymentException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PaymentExceptionHandler implements ResponseErrorHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        JsonNode rootNode = objectMapper.readTree(responseBody);
        String message = rootNode.path("message").asText();

        throw new PaymentException(response.getStatusCode(), message);
    }
}
