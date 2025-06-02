package roomescape.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.BadRequestException;
import roomescape.common.exception.InternalServerErrorException;
import roomescape.infrastructure.dto.request.ConfirmPaymentRequest;
import roomescape.infrastructure.dto.response.ConfirmPaymentResponse;
import roomescape.infrastructure.dto.PaymentFailure;

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
    @Retryable(retryFor = {InternalServerErrorException.class})
    public ResponseEntity<ConfirmPaymentResponse> postConfirmPayment(ConfirmPaymentRequest paymentRequest) {
        UUID idempotencyKey = UUID.randomUUID();
        return restClient.post()
                .uri("/confirm")
                .header("Idempotency-Key", idempotencyKey.toString())
                .body(paymentRequest)
                .retrieve()
                .toEntity(ConfirmPaymentResponse.class);
    }

    private void handlePaymentResponse(ConfirmPaymentResponse response) {
        if (response.failure() == null) {
            return ;
        }
        PaymentFailure failure = response.failure();
        if (IGNORE_CODES.contains(failure.code())) {
            // TODO: failure.message() Logging
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
        throw new BadRequestException(failure.message());
    }
}
