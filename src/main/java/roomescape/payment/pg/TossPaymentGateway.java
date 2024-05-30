package roomescape.payment.pg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentConfirmFailException;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TossPaymentGateway implements PaymentGateway {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.tosspayments.com/v1/payments")
            .build();

    @Value("${custom.pg.widget-secret-key}")
    private String widgetSecretKey;

    public PaymentConfirmResponse confirm(
            final String orderId,
            final Long amount,
            final String paymentKey
    ) {
        return restClient.post()
                .uri("/confirm")
                .header("Content-Type", "application/json")
                .header("Authorization", generateAuthorizations())
                .body(new PaymentConfirmRequest(orderId, amount, paymentKey))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new PaymentConfirmFailException(response.getStatusText());
                })
                .body(PaymentConfirmResponse.class);
    }

    private String generateAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
