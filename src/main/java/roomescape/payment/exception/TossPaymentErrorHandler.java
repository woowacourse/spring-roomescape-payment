package roomescape.payment.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TossPaymentErrorHandler implements RestClient.ResponseSpec.ErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(@NonNull HttpRequest request, ClientHttpResponse response) throws IOException {
        String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        HttpStatusCode statusCode = response.getStatusCode();

        String errorMessage = extractMessage(body);

        if (statusCode.is4xxClientError()) {
            throw new InvalidPaymentException(errorMessage, statusCode);
        }

        if (statusCode.is5xxServerError()) {
            throw new PaymentServerException(errorMessage, statusCode);
        }
    }

    private String extractMessage(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            return root.path("message").asText();
        } catch (Exception e) {
            return body;
        }
    }
}

