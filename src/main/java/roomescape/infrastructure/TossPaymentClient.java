package roomescape.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.PaymentException;
import roomescape.service.dto.PaymentRequest;

@Component
public class TossPaymentClient implements PaymentClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void requestApproval(PaymentRequest request) {
        try {
            restClient.post()
                    .uri("/v1/payments/confirm")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (PaymentException e) {
            throw e;
        } catch (ResourceAccessException e) {
            logger.error(e.getMessage(), e);
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 서버의 연결 가능 시간이 초과되었습니다.");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제를 할 수 없습니다.");
        }
    }
}
