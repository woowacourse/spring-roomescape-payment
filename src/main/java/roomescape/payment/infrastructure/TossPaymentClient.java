package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.dto.TossPaymentErrorResponse;
import roomescape.payment.infrastructure.dto.TossPaymentRequest;
import roomescape.payment.application.service.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.reservation.presentation.dto.ReservationRequest;

public class TossPaymentClient implements PaymentClient {

    @Value("${payment.secret-key.test}")
    private String SECRET_KEY;

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Payment approve(ReservationRequest reservationRequest) {
        TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(
                reservationRequest.getAmount(),
                reservationRequest.getOrderId(),
                reservationRequest.getPaymentKey()
        );

        return restClient.post()
                .uri("/payments/confirm")
                .header("Authorization", encodeSecretKey())
                .body(tossPaymentRequest)
                .retrieve()
                .onStatus(
                        status -> status.value() != 200,
                        (req, res) -> {
                                InputStream body = res.getBody();
                                TossPaymentErrorResponse errorResponse = objectMapper.readValue(body, TossPaymentErrorResponse.class);
                                throw new PaymentException(errorResponse, res.getStatusCode(), tossPaymentRequest.getOrderId());
                        }
                )
                .body(Payment.class);
    }

    private String encodeSecretKey() {
        String credentials = this.SECRET_KEY + ":";
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + base64Credentials;
    }
}
