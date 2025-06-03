package roomescape.infrastructure;

import static roomescape.domain.payment.TransactionStatusCode.FAILED_INTERNAL_PROCESSING;
import static roomescape.domain.payment.TransactionStatusCode.FAILED_PAYMENT;
import static roomescape.domain.payment.TransactionStatusCode.INVALID_AUTH_CREDENTIALS;

import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import roomescape.domain.payment.PaymentConfirmation;
import roomescape.domain.payment.PaymentDetails;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.TransactionStatus;
import roomescape.domain.payment.TransactionStatusCode;

public class TossPaymentProvider implements PaymentProvider {

    private final RestClient tossRestClient;
    private final String authorizationValue;
    private final Map<String, TransactionStatusCode> tossFailureCodes;

    public TossPaymentProvider(final RestClient.Builder builder, final String authorizationValue) {
        this.tossRestClient = builder.build();
        this.authorizationValue = authorizationValue;
        tossFailureCodes = initializeFailureCode();
    }

    @Override
    public PaymentDetails confirm(final PaymentRequest paymentRequest) {
        var confirmUri = "/v1/payments/confirm";
        return tossRestClient.post()
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

    private TransactionStatus convertToStatus(final FailureResponse tossResponse) {
        var failureCode = tossFailureCodes.getOrDefault(tossResponse.code(), FAILED_PAYMENT);
        return TransactionStatus.fail(failureCode, tossResponse.message());
    }

    private Map<String, TransactionStatusCode> initializeFailureCode() {
        return Map.ofEntries(
                Map.entry("INVALID_API_KEY", INVALID_AUTH_CREDENTIALS),
                Map.entry("UNAUTHORIZED_KEY", INVALID_AUTH_CREDENTIALS),
                Map.entry("INCORRECT_BASIC_AUTH_FORMAT", INVALID_AUTH_CREDENTIALS),
                Map.entry("FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", FAILED_INTERNAL_PROCESSING),
                Map.entry("FAILED_INTERNAL_SYSTEM_PROCESSING", FAILED_INTERNAL_PROCESSING),
                Map.entry("UNKNOWN_PAYMENT_ERROR", FAILED_INTERNAL_PROCESSING)
        );
    }

    private record FailureResponse(String code, String message) {
    }
}
