package roomescape.infrastructure;

import static roomescape.domain.payment.PaymentFailCode.EXTERNAL_SERVER_PROCESSING;
import static roomescape.domain.payment.PaymentFailCode.CONDITION_NOT_SATISFIED;
import static roomescape.domain.payment.PaymentFailCode.INVALID_AUTH_CREDENTIALS;

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
import roomescape.domain.payment.PaymentFailCode;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.PaymentFailure;

@Component
public class TossPaymentProvider implements PaymentProvider {

    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String NO_PASSWORD_SIGN = ":";
    private static final String AUTHORIZATION_HEADER_VALUE = "Basic " + base64Encode(WIDGET_SECRET_KEY + NO_PASSWORD_SIGN);

    private static final Map<String, PaymentFailCode> tossFailureCodeMappings = Map.ofEntries(
        Map.entry("INVALID_API_KEY", INVALID_AUTH_CREDENTIALS),
        Map.entry("UNAUTHORIZED_KEY", INVALID_AUTH_CREDENTIALS),
        Map.entry("INCORRECT_BASIC_AUTH_FORMAT", INVALID_AUTH_CREDENTIALS),
        Map.entry("FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", EXTERNAL_SERVER_PROCESSING),
        Map.entry("FAILED_INTERNAL_SYSTEM_PROCESSING", EXTERNAL_SERVER_PROCESSING),
        Map.entry("UNKNOWN_PAYMENT_ERROR", EXTERNAL_SERVER_PROCESSING)
    );

    private final RestClient restClient;

    public TossPaymentProvider(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentDetails confirm(final PaymentRequest paymentRequest) {
        var confirmUri = "/v1/payments/confirm";
        return restClient.post()
                .uri(confirmUri)
                .header("Authorization", AUTHORIZATION_HEADER_VALUE)
                .header("Content-Type", "application/json")
                .body(paymentRequest)
                .exchange((request, response) -> convertToDetails(response));
    }

    private PaymentDetails convertToDetails(final ConvertibleClientHttpResponse tossResponse) throws IOException {
        if (HttpStatus.OK == tossResponse.getStatusCode()) {
            var confirmation = tossResponse.bodyTo(PaymentConfirmation.class);
            return new PaymentDetails(confirmation);
        }
        var tossFailureResponse = tossResponse.bodyTo(TossFailureResponse.class);
        var paymentFailure = convertToPaymentFailure(tossFailureResponse);
        return new PaymentDetails(paymentFailure);
    }

    private PaymentFailure convertToPaymentFailure(final TossFailureResponse tossResponse) {
        var failureCode = tossFailureCodeMappings.getOrDefault(tossResponse.code(), CONDITION_NOT_SATISFIED);
        return new PaymentFailure(failureCode, tossResponse.message());
    }

    private record TossFailureResponse(String code, String message) {}

    private static String base64Encode(final String string) {
        var base64Encoder = Base64.getEncoder();
        var encodedBytes = base64Encoder.encode(string.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }
}
