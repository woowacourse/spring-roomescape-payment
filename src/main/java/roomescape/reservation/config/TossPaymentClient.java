package roomescape.reservation.config;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.PaymentApprovalRequest;

@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final PaymentErrorHandler errorHandler;

    public TossPaymentClient(
            RestClient restClient,
            PaymentErrorHandler errorHandler
    ) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    @Override
    public ResponseEntity<Void> approvePayment(PaymentApprovalRequest request) {
        return restClient.post().uri("/confirm")
                .body(request)
                .retrieve()
                .onStatus(errorHandler)
                .toBodilessEntity();
    }
}
