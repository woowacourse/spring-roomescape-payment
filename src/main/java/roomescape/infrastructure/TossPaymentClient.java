package roomescape.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.BadRequestException;
import roomescape.common.exception.InternalServerErrorException;
import roomescape.infrastructure.dto.PaymentFailure;
import roomescape.infrastructure.dto.request.ConfirmPaymentRequest;
import roomescape.infrastructure.dto.response.ConfirmPaymentResponse;

import java.util.List;
import java.util.UUID;

@Component
public class TossPaymentClient {
    private static final List<String> IGNORE_CODES = List.of(
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH"
    );

    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    // TODO: 결제 실패시 환불
    @Retryable
    public ResponseEntity<ConfirmPaymentResponse> postConfirmPayment(ConfirmPaymentRequest paymentRequest, UUID idempotencyKey) {
        return restClient.post()
                .uri("/confirm")
                .header("Idempotency-Key", idempotencyKey.toString())
                .body(paymentRequest)
                .retrieve()
                .toEntity(ConfirmPaymentResponse.class);
    }

    public void validateResponse(ResponseEntity<ConfirmPaymentResponse> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }

        PaymentFailure failure = response.getBody().failure();

        if (failure == null || is5xxResponse(response)) {
            throw new InternalServerErrorException();
        }

        if (is4xxResponse(response)) {
            throw new BadRequestException(failure.message());
        }
    }

    private boolean is4xxResponse(ResponseEntity<ConfirmPaymentResponse> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return false;
        }
        PaymentFailure failure = response.getBody().failure();
        return !IGNORE_CODES.contains(failure.code());
    }

    private boolean is5xxResponse(ResponseEntity<ConfirmPaymentResponse> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return false;
        }
        PaymentFailure failure = response.getBody().failure();
        return IGNORE_CODES.contains(failure.code());
    }
}
