package roomescape.reservation.service;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.response.PaymentApproveResponse;
import roomescape.reservation.entity.Payment;
import roomescape.reservation.error.handler.PaymentResponseErrorHandler;

@Component
public class PaymentRestClient {

    public static final String TOSS_PAYMENT_URL = "https://api.tosspayments.com/v1/payments";

    private final RestClient restClient;
    private final PaymentResponseErrorHandler paymentResponseErrorHandler;
    private final String testKey;

    public PaymentRestClient(
            RestClient.Builder builder,
            PaymentResponseErrorHandler paymentResponseErrorHandler,
            @Value("${toss.secret-key}") String testKey
    ) {
        this.restClient = builder
                .baseUrl(TOSS_PAYMENT_URL)
                .build();
        this.paymentResponseErrorHandler = paymentResponseErrorHandler;
        this.testKey = testKey;
    }

    public PaymentApproveResponse approve(Payment payment) {
        return restClient.post()
                .uri("/confirm")
                .header("Authorization", "Basic " + getEncodedKey())
                .body(payment)
                .retrieve()
                .onStatus(paymentResponseErrorHandler)
                .body(PaymentApproveResponse.class);
    }

    private String getEncodedKey() {
        return Base64.getEncoder().encodeToString(testKey.getBytes());
    }
}
