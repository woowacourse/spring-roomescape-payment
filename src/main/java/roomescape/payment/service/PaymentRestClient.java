package roomescape.payment.service;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.entity.Payment;
import roomescape.payment.error.handler.PaymentResponseErrorHandler;

@Component
@RequiredArgsConstructor
public class PaymentRestClient {

    private final RestClient restClient;
    private final PaymentResponseErrorHandler paymentResponseErrorHandler;

    @Value("${toss.secret-key}")
    private String testKey;

    public void approve(Payment payment) {
        restClient.post()
                .uri("/confirm")
                .header("Authorization", "Basic " + getEncodedKey())
                .body(payment)
                .retrieve()
                .onStatus(paymentResponseErrorHandler)
                .body(Payment.class);
    }

    private String getEncodedKey() {
        return Base64.getEncoder().encodeToString(testKey.getBytes());
    }
}
