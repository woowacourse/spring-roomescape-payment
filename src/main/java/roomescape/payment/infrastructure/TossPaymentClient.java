package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.reservation.exception.RequestPaymentErrorHandler;
import roomescape.payment.domain.PaymentClient;
import roomescape.reservation.presentation.dto.PaymentRequest;
import roomescape.payment.infrastructure.dto.PaymentResponse;

@Component
public class TossPaymentClient implements PaymentClient {

    private String secretKey;
    private final RestClient restClient;

    public TossPaymentClient(
        @Value("payment.toss.secret-key")String secretKey, RestClient restClient) {
        this.secretKey = secretKey;
        this.restClient = restClient;
    }

    @Override
    public PaymentResponse requestPayment(final PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + encodeSecretKey())
                .body(paymentRequest)
                .retrieve()
                .onStatus(new RequestPaymentErrorHandler(new ObjectMapper()))
                .body(PaymentResponse.class);
    }

    private String encodeSecretKey() {
        if (secretKey == null) {
            throw new IllegalStateException("Secret key is not properly injected");
        }
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }
}
