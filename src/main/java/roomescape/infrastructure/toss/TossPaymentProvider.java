package roomescape.infrastructure.toss;

import static roomescape.domain.payment.TransactionStatusCode.FAILED_INTERNAL_PROCESSING;
import static roomescape.domain.payment.TransactionStatusCode.FAILED_PAYMENT;
import static roomescape.domain.payment.TransactionStatusCode.INVALID_AUTH_CREDENTIALS;

import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import roomescape.domain.payment.PaymentConfirmation;
import roomescape.domain.payment.PaymentExecutionResult;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.TransactionStatus;
import roomescape.domain.payment.TransactionStatusCode;

@Slf4j
public class TossPaymentProvider implements PaymentProvider {

    public static final String CONFIRM_ENDPOINT = "/v1/payments/confirm";

    private final RestClient tossRestClient;
    private final String authorizationValue;
    private final Map<String, TransactionStatusCode> tossFailureCodes;

    public TossPaymentProvider(final RestClient.Builder builder, final String authorizationValue) {
        this.tossRestClient = builder.build();
        this.authorizationValue = authorizationValue;
        tossFailureCodes = initializeFailureCode();
    }

    @Override
    public PaymentExecutionResult confirm(final PaymentRequest paymentRequest) {
        log.info("토스 페이먼츠에 결제 승인 요청 시작. orderId: {}, paymentKey: {}, amount: {}",
                paymentRequest.orderId(),
                paymentRequest.paymentKey(),
                paymentRequest.amount()
        );

        return tossRestClient.post()
                .uri(CONFIRM_ENDPOINT)
                .header("Authorization", authorizationValue)
                .header("Content-Type", "application/json")
                .body(paymentRequest)
                .exchange((request, response) -> convertToDetails(response));
    }

    private PaymentExecutionResult convertToDetails(final ConvertibleClientHttpResponse response) throws IOException {
        if (HttpStatus.OK == response.getStatusCode()) {
            log.info("결제 승인 성공 응답 반환됨");
            var confirmation = response.bodyTo(PaymentConfirmation.class);
            return new PaymentExecutionResult(confirmation);
        }
        var tossResponse = response.bodyTo(FailureResponse.class);
        log.error("결제 승인 실패 응답 반환됨. 응답 상태코드: {}, 토스 에러코드: {}", response.getStatusCode(), tossResponse.code());
        var status = convertToStatus(tossResponse);
        return new PaymentExecutionResult(status);
    }

    private TransactionStatus convertToStatus(final FailureResponse tossResponse) {
        var failureCode = tossFailureCodes.getOrDefault(tossResponse.code(), FAILED_PAYMENT);
        log.info("결제 트랜잭션 결과 상태 코드: {}", failureCode);
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
