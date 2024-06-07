package roomescape.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.PaymentResponse;
import roomescape.exception.PaymentException;
import roomescape.service.dto.PaymentConfirmRequest;

public class TossPaymentClient implements PaymentClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestClient restClient;
    private final PaymentProperties paymentProperties;

    public TossPaymentClient(RestClient restClient, PaymentProperties paymentProperties) {
        this.restClient = restClient;
        this.paymentProperties = paymentProperties;
    }

    @Override
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        try {
            ResponseEntity<PaymentResponse> response = restClient.post()
                    .uri(paymentProperties.url().paymentConfirm())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(PaymentResponse.class);
            return response.getBody();
        } catch (ResourceAccessException e) {
            logger.error("payment request body = {}" , request, e);
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 서버의 연결 가능 시간이 초과되었습니다.");
        }
    }
}
