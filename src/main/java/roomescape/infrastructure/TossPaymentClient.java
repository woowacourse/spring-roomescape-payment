package roomescape.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentException;
import roomescape.service.dto.PaymentRequest;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void requestApproval(PaymentRequest request) {
        try {
            restClient.post()
                    .uri("/v1/payments/confirm")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 서버의 연결 가능 시간이 초과되었습니다.");
        }
    }
}
