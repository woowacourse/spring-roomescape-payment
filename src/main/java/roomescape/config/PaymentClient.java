package roomescape.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentCancelResponse;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.dto.ReservationCancelRequest;

import java.util.Base64;

@Component
public class PaymentClient {
    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);
    private static final String PAYMENT_CONFIRM_URI = "/v1/payments/confirm";

    private final String secretKey;
    private final RestClient restClient;

    public PaymentClient(@Value("${security.payment.secret-key}") String secretKey, RestClient restClient) {
        this.secretKey = secretKey;
        this.restClient = restClient;
    }

    public PaymentResponse approve(PaymentRequest request) {
        PaymentResponse response = restClient.post()
                .uri(PAYMENT_CONFIRM_URI)
                .header("Authorization", "Basic " + getAuthorization())
                .body(request)
                .retrieve()
                .onStatus(new PaymentClientResponseErrorHandler())
                .body(PaymentResponse.class);

        log.info("URI: {}, Method: {}, request: {} response: {}", PAYMENT_CONFIRM_URI, "POST", request, response);
        return response;
    }

    public PaymentCancelResponse refund(String paymentKey, ReservationCancelRequest request) {
        PaymentCancelResponse response = restClient.post()
                .uri(String.format("/v1/payments/%s/cancel", paymentKey))
                .header("Authorization", "Basic " + getAuthorization())
                .body(request)
                .retrieve()
                .onStatus(new PaymentClientResponseErrorHandler())
                .body(PaymentCancelResponse.class);

        log.info("cancel request: {}, response: {}", request, response);
        return response;
    }

    private String getAuthorization() {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString((secretKey + ":").getBytes());
    }
}
