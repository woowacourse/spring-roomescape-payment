package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import roomescape.reservation.exception.PaymentRequestException;
import roomescape.reservation.exception.RequestPaymentErrorHandler;
import roomescape.reservation.presentation.dto.PaymentRequest;
import roomescape.reservation.presentation.dto.PaymentResponse;

@Service
public class PaymentService {

    @Value("${toss.secret.key}")
    private String secretKey;
    private final RestClient restClient;

    public PaymentService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .build();
    }

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
