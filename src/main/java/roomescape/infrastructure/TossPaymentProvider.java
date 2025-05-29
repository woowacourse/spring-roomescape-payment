package roomescape.infrastructure;

import static roomescape.domain.payment.PaymentStatusCode.FAILED_INTERNAL_PROCESSING;
import static roomescape.domain.payment.PaymentStatusCode.FAILED_PAYMENT;
import static roomescape.domain.payment.PaymentStatusCode.INVALID_AUTH_CREDENTIALS;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import roomescape.domain.payment.PaymentConfirmation;
import roomescape.domain.payment.PaymentDetails;
import roomescape.domain.payment.PaymentStatusCode;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.PaymentStatus;

@Component
public class TossPaymentProvider implements PaymentProvider {

    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String NO_PASSWORD_SIGN = ":";
    private static final String AUTHORIZATION_PREFIX = "Basic ";

    private final RestClient restClient;
    private final String authorizationValue;
    private final Map<String, PaymentStatusCode> tossFailureCodes;

    public TossPaymentProvider(final RestClient restClient) {
        this.restClient = restClient;
        authorizationValue = AUTHORIZATION_PREFIX + encodeSecretKey();
        tossFailureCodes = initializeFailureCode();
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
        var tossResponse = response.bodyTo(FailureResponse.class);
        var status = convertToStatus(tossResponse);
        return new PaymentDetails(status);
    }

    private PaymentStatus convertToStatus(final FailureResponse tossResponse) {
        var failureCode = tossFailureCodes.getOrDefault(tossResponse.code(), FAILED_PAYMENT);
        return PaymentStatus.fail(failureCode, tossResponse.message());
    }

    private String encodeSecretKey() {
        var base64Encoder = Base64.getEncoder();
        var encodedBytes = base64Encoder.encode((WIDGET_SECRET_KEY + NO_PASSWORD_SIGN).getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }

    private Map<String, PaymentStatusCode> initializeFailureCode() {
        return Map.ofEntries(
                Map.entry("INVALID_API_KEY", INVALID_AUTH_CREDENTIALS),
                Map.entry("UNAUTHORIZED_KEY", INVALID_AUTH_CREDENTIALS),
                Map.entry("INCORRECT_BASIC_AUTH_FORMAT", INVALID_AUTH_CREDENTIALS),
                Map.entry("FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", FAILED_INTERNAL_PROCESSING),
                Map.entry("FAILED_INTERNAL_SYSTEM_PROCESSING", FAILED_INTERNAL_PROCESSING),
                Map.entry("UNKNOWN_PAYMENT_ERROR", FAILED_INTERNAL_PROCESSING)
        );
    }

    private record FailureResponse(String code, String message) {}
}
