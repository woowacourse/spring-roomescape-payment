package roomescape.infrastructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import roomescape.domain.payment.PaymentConfirmation;
import roomescape.domain.payment.PaymentDetails;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.PaymentStatus;

@Component
public class TossPaymentProvider implements PaymentProvider {

    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String NO_PASSWORD_SIGN = ":";
    private static final String TOSS_API_BASE_URL = "https://api.tosspayments.com";
    private static final String AUTHORIZATION_PREFIX = "Basic ";

    private final RestClient restClient;
    private final String authorizationValue;

    public TossPaymentProvider() {
        restClient = RestClient.create(TOSS_API_BASE_URL);
        authorizationValue = AUTHORIZATION_PREFIX + encodeSecretKey();
    }

    @Override
    public PaymentDetails confirm(final PaymentRequest paymentRequest) {
        var confirmUri = "/v1/payments/confirm";
        return restClient.post()
            .uri(confirmUri)
            .header("Authorization", authorizationValue)
            .header("Content-Type", "application/json")
            .body(paymentRequest)
            .exchange((request, response) -> convertToDetails(response));
    }

    private PaymentDetails convertToDetails(final ConvertibleClientHttpResponse response) throws IOException {
        if (HttpStatus.OK == response.getStatusCode()) {
            var confirmation = response.bodyTo(PaymentConfirmation.class);
            return new PaymentDetails(confirmation);
        }
        var status = response.bodyTo(PaymentStatus.class);
        return new PaymentDetails(status);
    }

    private String encodeSecretKey() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((WIDGET_SECRET_KEY + NO_PASSWORD_SIGN).getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }
}
