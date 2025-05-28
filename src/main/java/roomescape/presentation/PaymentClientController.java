package roomescape.presentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.domain.PaymentInfo;
import roomescape.dto.PaymentRequest;
import roomescape.exception.FilteredPaymentException;
import roomescape.exception.PaymentException;

import java.util.Arrays;
import java.util.List;

public class PaymentClientController {

    private static final List<String> CODES = List.of("INVALID_API_KEY", "UNAUTHORIZED_KEY", "INCORRECT_BASIC_AUTH_FORMAT");

    private final RestClient restClient;

    public PaymentClientController(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentInfo postPaymentInfo(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(paymentRequest)
                .retrieve()
                .onStatus(status ->
                        status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode node = objectMapper.readTree(response.getBody());
                    String message = node.get("message").asText();
                    String code = node.get("code").asText();
                    if (CODES.contains(code)) {
                        throw new FilteredPaymentException();
                    }
                    throw new PaymentException(message, response.getStatusCode());
                })
                .body(PaymentInfo.class);
    }
}
