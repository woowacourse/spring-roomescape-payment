package roomescape.payment.config;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

@Component
public class PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);
    private final String secretKey;
    private final String paymentUrl;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentClient(@Value("${security.payment.secret-key}") String secretKey,
                         @Value("${security.payment.url}") String paymentUrl,
                         RestClient.Builder restClientBuilder,
                         ObjectMapper objectMapper) {
        this.secretKey = secretKey;
        this.paymentUrl = paymentUrl;
        this.restClient = restClientBuilder
                .baseUrl(paymentUrl)
                .build();
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
