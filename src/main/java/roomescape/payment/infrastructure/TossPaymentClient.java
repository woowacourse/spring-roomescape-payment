package roomescape.payment.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.dto.request.ConfirmPaymentRequest;
import roomescape.payment.infrastructure.dto.response.ConfirmPaymentResponse;

import java.util.UUID;

@Component
public class TossPaymentClient {

    private final RestClient restClient;
    private final TossPaymentErrorHandler tossPaymentErrorHandler;

    public TossPaymentClient(RestClient restClient, TossPaymentErrorHandler tossPaymentErrorHandler) {
        this.restClient = restClient;
        this.tossPaymentErrorHandler = tossPaymentErrorHandler;
    }

    // TODO: 결제 실패시 환불
    @Retryable(retryFor = {HttpServerErrorException.class})
    public ResponseEntity<ConfirmPaymentResponse> postConfirmPayment(ConfirmPaymentRequest paymentRequest, UUID idempotencyKey) {
        return restClient.post()
                .uri("/confirm")
                .header("Idempotency-Key", idempotencyKey.toString())
                .body(paymentRequest)
                .retrieve()
                .onStatus(tossPaymentErrorHandler)
                .toEntity(ConfirmPaymentResponse.class);
    }
}
