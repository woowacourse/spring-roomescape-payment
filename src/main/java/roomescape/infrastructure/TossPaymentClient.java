package roomescape.infrastructure;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.PaymentErrorMessage;
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
            throw new PaymentException(PaymentErrorMessage.TIME_OUT);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new PaymentException(PaymentErrorMessage.DEFAULT);
        }
    }

    @Override
    public void requestRefund(String paymentKey) {
        Map<String, String> params = Map.of("cancelReason", "사장님 권한 취소");

        try {
            restClient.post()
                    .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
                    .body(params)
                    .retrieve()
                    .toBodilessEntity();
        } catch (PaymentException e) {
            throw e;
        } catch (ResourceAccessException e) {
            logger.error(e.getMessage(), e);
            throw new PaymentException(PaymentErrorMessage.TIME_OUT);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new PaymentException(PaymentErrorMessage.DEFAULT);
        }
    }
}
