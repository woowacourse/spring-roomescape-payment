package roomescape.reservation.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.reservation.service.PaymentApprovalRequest;

@Component
public class TossPaymentClient implements PaymentClient {
    private static final String URI_PATH = "/v1/payments/confirm";

    private final String baseUrl;
    private final RestClient restClient;
    private final PaymentErrorHandler errorHandler;

    public TossPaymentClient(
            @Value("${payment.toss.base-url}") String baseUrl,
            RestClient restClient,
            PaymentErrorHandler errorHandler
    ) {
        this.baseUrl = baseUrl;
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    @Override
    public ResponseEntity<Void> approvePayment(PaymentApprovalRequest request) {
        return restClient.post().uri(baseUrl + URI_PATH)
                .body(request)
                .retrieve()
                .onStatus(errorHandler)
                .toBodilessEntity();
    }
}
