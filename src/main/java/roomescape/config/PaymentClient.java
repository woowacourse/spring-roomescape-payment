package roomescape.config;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;

@Component
public class PaymentClient {

    private final String secretKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentClient(@Value("${security.payment.secret-key}") String secretKey, RestClient restClient, ObjectMapper objectMapper) {
        this.secretKey = secretKey;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void approve(PaymentRequest paymentRequest) {
        String basic = getAuthorization();
        restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + basic)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new PaymentClientResponseErrorHandler())
                .toBodilessEntity();
    }

    public PaymentResponse readPayment(String paymentKey) {
        String basic = getAuthorization();
        String response = restClient.get()
                .uri("v1/payments/{paymentKey}", paymentKey)
                .header("Authorization", "Basic " + basic)
                .retrieve()
                .body(String.class);
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            return objectMapper.treeToValue(rootNode, PaymentResponse.class);
        } catch (JsonProcessingException e) {
            throw new RoomescapeException(ExceptionType.INVALID_PARSE_FORMAT);
        }
    }

    private String getAuthorization() {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString((secretKey + ":").getBytes());
    }
}
