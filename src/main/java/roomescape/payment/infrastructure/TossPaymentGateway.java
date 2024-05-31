package roomescape.payment.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentConfirmFailException;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

@Component
public class TossPaymentGateway implements PaymentGateway {

    private static final String AUTHORIZATION_HEADER = "Basic ";

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.tosspayments.com/v1/payments")
            .build();

    @Value("${custom.pg.widget-secret-key}")
    private String widgetSecretKey;

    @Override
    public PaymentConfirmResponse confirm(
            final String orderId,
            final Long amount,
            final String paymentKey
    ) {
        return restClient.post()
                .uri("/confirm")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, generateAuthorizations())
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
        return AUTHORIZATION_HEADER + new String(encodedBytes);
    }
}
