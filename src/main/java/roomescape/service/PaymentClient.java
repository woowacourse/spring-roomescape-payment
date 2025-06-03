package roomescape.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.PaymentInfo;
import roomescape.dto.PaymentRequest;
import roomescape.exception.FilteredPaymentException;
import roomescape.exception.PaymentException;

import java.util.List;

@Component
public class PaymentClient {

    private static final List<String> CODES = List.of("INVALID_API_KEY", "UNAUTHORIZED_KEY", "INCORRECT_BASIC_AUTH_FORMAT");

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentClient(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public PaymentInfo postPaymentInfo(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(paymentRequest)
                .retrieve()
                .onStatus(status ->
                        status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
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
