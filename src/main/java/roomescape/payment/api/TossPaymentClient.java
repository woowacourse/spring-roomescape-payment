package roomescape.payment.api;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.payment.config.PaymentClientResponseErrorHandler;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

@Component
public class TossPaymentClient implements PaymentClient {
    private static final String DELIMITER = ":";
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_METHOD = "Basic ";
    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);
    private final String encodedSecretKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(@Value("${security.toss.payment.secret-key}") String secretKey,
                             @Value("${security.toss.payment.url}") String paymentUrl,
                             RestClient.Builder restClientBuilder,
                             ObjectMapper objectMapper) {
        this.encodedSecretKey = Base64.getEncoder()
                .encodeToString((secretKey + DELIMITER).getBytes());
        this.restClient = restClientBuilder
                .baseUrl(paymentUrl)
                .build();
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentResponse payment(PaymentRequest paymentRequest) {
        String uri = "/v1/payments/confirm";
        PaymentResponse paymentResponse = restClient.post()
                .uri(uri)
                .header(AUTH_HEADER, AUTH_METHOD + encodedSecretKey)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new PaymentClientResponseErrorHandler(objectMapper))
                .body(PaymentResponse.class);
        log.info("URI: {}, Method: {}, Body:{} ", uri, "POST", paymentResponse);
        return paymentResponse;
    }
}
