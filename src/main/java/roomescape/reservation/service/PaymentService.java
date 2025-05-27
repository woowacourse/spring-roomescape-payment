package roomescape.reservation.service;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PaymentService {

    private final String secretKey;
    private final RestClient restClient;

    public PaymentService(
            @Value("${payment.toss.secret-key}") String secretKey,
            RestClient restClient
    ) {
        this.secretKey = Base64.getEncoder().encodeToString((secretKey + ":base64").getBytes());
        this.restClient = restClient;
    }

    public TossPaymentConfirmResponse processPayment(
            final String paymentKey,
            final String orderId,
            final int amount
    ) {
        final String uri = "https://api.tosspayments.com/v1/payments/confirm";

        return restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + secretKey)
                .body(new TossPaymentConfirmRequest(orderId, amount, paymentKey))
                .retrieve()
                .body(TossPaymentConfirmResponse.class);
    }
}
