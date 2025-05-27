package roomescape.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import roomescape.domain.PaymentInfo;
import roomescape.dto.PaymentRequest;
import roomescape.exception.PaymentErrorResponse;
import roomescape.exception.PaymentException;

import java.io.IOException;

public class PaymentClientService {
    private final RestClient restClient;

    public PaymentClientService(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentInfo postPaymentInfo(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(paymentRequest)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode node = objectMapper.readTree(response.getBody());
                    String message = node.get("message").asText();
                    throw new PaymentException(message, response.getStatusCode());
                })
                .body(PaymentInfo.class);
    }
}
