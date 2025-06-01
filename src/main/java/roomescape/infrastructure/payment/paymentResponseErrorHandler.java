package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Set;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.FilteredPaymentException;
import roomescape.exception.PaymentException;

@Component
public class paymentResponseErrorHandler implements ResponseErrorHandler {

    private static final Set<String> CODES = Set.of("INVALID_API_KEY", "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    public void handleError(ClientHttpResponse response, HttpStatusCode statusCode) throws IOException {
        JsonNode node = objectMapper.readTree((response.getBody()));
        String message = node.get("message").asText();
        String code = node.get("code").asText();
        if (CODES.contains(code)) {
            throw new FilteredPaymentException();
        }
        throw new PaymentException(message, statusCode);
    }
}
