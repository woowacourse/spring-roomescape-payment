package roomescape.payment.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.PaymentException;
import roomescape.payment.application.dto.TossPaymentRequest;
import roomescape.payment.application.service.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.presentation.dto.PaymentRequest;

public class TossPaymentClient implements PaymentClient {

    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private final RestClient restClient;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Payment approve(PaymentRequest paymentRequest) {
        TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(
                paymentRequest.getAmount(),
                paymentRequest.getOrderId(),
                paymentRequest.getPaymentKey()
        );

        return restClient.post()
                .uri("/payments/confirm")
                .header("Authorization", encodeSecretKey(SECRET_KEY))
                .body(tossPaymentRequest)
                .retrieve()
                .onStatus((status) -> status.value() != 200, (req, res) -> {
                    throw new PaymentException(res.getStatusText());
                })
                .body(Payment.class);
    }

    public static String encodeSecretKey(String secretKey) {
        String credentials = secretKey + ":";
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + base64Credentials;
    }
}
