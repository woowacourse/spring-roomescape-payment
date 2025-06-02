package roomescape.reservation.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import roomescape.reservation.service.PaymentApprovalRequest;

@Component
@RequiredArgsConstructor
public class TossPaymentClient implements PaymentClient {

    private static final String AUTH_HEADER_NAME = "Authorization";

    private final RestClient restClient;
    private final PaymentErrorHandler errorHandler;
    private final TossPaymentProperties properties;

    @Override
    public ResponseEntity<Void> approvePayment(PaymentApprovalRequest request) {
        return restClient.post()
                .uri(properties.getBaseUrl() + "/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, properties.getAuthToken())
                .body(request)
                .retrieve()
                .onStatus(errorHandler)
                .toBodilessEntity();
    }
}
