package roomescape.reservation.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import roomescape.reservation.service.PaymentApprovalRequest;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String AUTH_HEADER_NAME = "Authorization";

    private final String authorizationToken;
    private final RestClient restClient;
    private final PaymentErrorHandler errorHandler;

    public TossPaymentClient(
            @Value("${payment.toss.auth-token}") String authorizationToken,
            RestClient restClient,
            PaymentErrorHandler errorHandler
    ) {
        this.authorizationToken = authorizationToken;
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    @Override
    public ResponseEntity<Void> approvePayment(PaymentApprovalRequest request) {
        return restClient.post().uri("https://api.tosspayments.com/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, authorizationToken)
                .body(request)
                .retrieve()
                .onStatus(errorHandler)
                .toBodilessEntity();
    }
}
