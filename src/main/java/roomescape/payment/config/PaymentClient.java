package roomescape.payment.config;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

@Component
public class PaymentClient {
    private static final String DELIMITER = ":";
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_METHOD = "Basic ";
    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);
    private final String encodedSecretKey;
    private final String paymentUrl;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentClient(@Value("${security.payment.secret-key}") String secretKey,
                         @Value("${security.payment.url}") String paymentUrl,
                         RestClient.Builder restClientBuilder,
                         ObjectMapper objectMapper) {
        this.encodedSecretKey = Base64.getEncoder()
                .encodeToString((secretKey + DELIMITER).getBytes());
        this.paymentUrl = paymentUrl;
        this.restClient = restClientBuilder
                .baseUrl(paymentUrl)
                .build();
        this.objectMapper = objectMapper;
    }

    public PaymentResponse payment(PaymentRequest paymentRequest) {
        String uri = "/v1/payments/confirm";
        PaymentResponse paymentResponse = restClient.post()
                .uri(uri)
                .header(AUTH_HEADER, AUTH_METHOD + encodedSecretKey)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new PaymentClientResponseErrorHandler(objectMapper))
                .body(PaymentResponse.class);
        log.info("URI: {}, Method: {}, Body:{} ", uri, HttpMethod.POST, paymentResponse);
        return paymentResponse;
    }
}
