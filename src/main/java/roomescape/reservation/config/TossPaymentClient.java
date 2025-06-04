package roomescape.reservation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.PaymentClientException;
import roomescape.global.exception.RoomescapeException;
import roomescape.reservation.dto.PaymentApprovalRequest;

@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    @Value("${payment.toss.approval-url}")
    private String approvalUrl;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @Retryable(
            retryFor = {PaymentClientException.class, RoomescapeException.class},
            backoff = @Backoff
    )
    public ResponseEntity<Void> approvePayment(PaymentApprovalRequest request) {
        return restClient.post().uri(approvalUrl)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
