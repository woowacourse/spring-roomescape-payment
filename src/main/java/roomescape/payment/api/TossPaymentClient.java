package roomescape.payment.api;

import java.util.Base64;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import roomescape.payment.config.PaymentClientResponseErrorHandler;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

@Component
public class TossPaymentClient implements PaymentClient {
    private static final String DELIMITER = ":";
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_METHOD = "Basic ";
    private static final String APPROVE_PAYMENT_URI = "/v1/payments/confirm";
    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);
    private final String encodedSecretKey;
    private final RestClient restClient;
    private final PaymentClientResponseErrorHandler paymentClientResponseErrorHandler;

    public TossPaymentClient(@Value("${security.toss.payment.secret-key}") String secretKey,
                             @Value("${security.toss.payment.url}") String paymentUrl,
                             RestClient.Builder restClientBuilder,
                             PaymentClientResponseErrorHandler paymentClientResponseErrorHandler) {
        this.encodedSecretKey = Base64.getEncoder()
                .encodeToString((secretKey + DELIMITER).getBytes());
        this.restClient = restClientBuilder
                .baseUrl(paymentUrl)
                .build();
        this.paymentClientResponseErrorHandler = paymentClientResponseErrorHandler;
    }

    @Override
    public PaymentResponse payment(PaymentRequest paymentRequest) {
        String requestId = UUID.randomUUID().toString();
        log.info("RequestID: {}, URI: {}, RequestBody:{} ", requestId, APPROVE_PAYMENT_URI, paymentRequest);
        PaymentResponse paymentResponse = restClient.post()
                .uri(APPROVE_PAYMENT_URI)
                .header(AUTH_HEADER, AUTH_METHOD + encodedSecretKey)
                .header("Request-ID", requestId)
                .body(paymentRequest)
                .retrieve()
                .onStatus(paymentClientResponseErrorHandler)
                .body(PaymentResponse.class);
        log.info("RequestID: {}, URI: {}, Method: {}, ResponseBody:{} ", requestId, APPROVE_PAYMENT_URI, "POST", paymentResponse);
        return paymentResponse;
    }
}
