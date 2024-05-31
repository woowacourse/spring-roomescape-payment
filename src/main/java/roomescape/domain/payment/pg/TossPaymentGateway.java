package roomescape.domain.payment.pg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.dto.PaymentConfirmRequest;
import roomescape.domain.payment.dto.PaymentConfirmResponse;
import roomescape.exception.PaymentConfirmFailException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static roomescape.domain.config.PaymentConfig.PG_API_BASE_URL;
import static roomescape.domain.config.PaymentConfig.PG_CONFIRM_API_URL;

@Component
public class TossPaymentGateway implements PaymentGateway {

    private final RestClient restClient = RestClient.builder()
            .baseUrl(PG_API_BASE_URL)
            .build();

    @Value("${custom.pg.widget-secret-key}")
    private String widgetSecretKey;

    public PaymentConfirmResponse confirm(
            final String orderId,
            final Long amount,
            final String paymentKey
    ) {
        return restClient.post()
                .uri(PG_CONFIRM_API_URL)
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
