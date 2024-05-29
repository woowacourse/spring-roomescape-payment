package roomescape.config;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);
    private final String secretKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentClient(@Value("${security.payment.secret-key}") String secretKey, RestClient restClient, ObjectMapper objectMapper) {
        this.secretKey = secretKey;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void approve(PaymentRequest paymentRequest) {
        String uri = "/v1/payments/confirm";
        String basic = getAuthorization();
        restClient.post()
                .uri(uri)
                .header("Authorization", "Basic " + basic)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new PaymentClientResponseErrorHandler())
                .toBodilessEntity();
        logging(uri, "POST", paymentRequest);
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
            PaymentResponse paymentResponse = objectMapper.treeToValue(rootNode, PaymentResponse.class);
            logging("v1/payments/" + paymentKey, "GET", paymentResponse);
            return paymentResponse;
        } catch (JsonProcessingException e) {
            throw new RoomescapeException(ExceptionType.INVALID_PARSE_FORMAT);
        }
    }

    private void logging(String uri, String httpMethod, Object body) {
        log.info("URI: {}, Method: {}, Body:{} ", uri, httpMethod, body);
    }

    private String getAuthorization() {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString((secretKey + ":").getBytes());
    }
}
