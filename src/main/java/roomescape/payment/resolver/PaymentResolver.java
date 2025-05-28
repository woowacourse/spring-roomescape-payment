package roomescape.payment.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
public class PaymentResolver {

    private final RestClient restClient;

    @Value("${payment.secret.key}")
    private String secretKey;

    public PaymentResponse execute(final String paymentKey,
                                   final int amount,
                                   final String orderId,
                                   final String paymentType) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);
        return restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", authorizations)
                .body(new PaymentRequest(paymentKey, amount, orderId, paymentType))
                .retrieve()
//                .onStatus(status -> status.value() == 404, (req, res) -> {
//                    throw new IllegalArgumentException();
//                })
                .body(PaymentResponse.class);
    }
}
