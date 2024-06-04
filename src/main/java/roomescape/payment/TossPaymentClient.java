package roomescape.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.global.exception.IllegalRequestException;
import roomescape.global.exception.PaymentFailException;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.dto.response.TossErrorResponse;

public class TossPaymentClient {


    @Value("${roomescape.payment.toss.confirm-endpoint-v1}")
    private String paymentConfirmEndpointV1;

    @Value("${roomescape.payment.toss.confirm-widget-secret-key}")
    private String widgetSecretKey;

    @Value("${roomescape.payment.toss.confirm-password}")
    private String password;

    private final RestClient restClient;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentConfirmResponse confirmPayments(PaymentConfirmRequest request) {
        try {
            return restClient.post().uri(paymentConfirmEndpointV1)
                    .headers(headers -> headers.setBasicAuth(widgetSecretKey, password))
                    .body(request)
                    .retrieve()
                    .toEntity(PaymentConfirmResponse.class)
                    .getBody();
        } catch (RestClientResponseException e) {
            TossErrorResponse errorResponse = e.getResponseBodyAs(TossErrorResponse.class);
            if (errorResponse.isClientError()) {
                log.error("Toss Payment Client Error 발생: {}", errorResponse.message(), e);
                throw new IllegalRequestException(errorResponse.message(), e);
            }
            throw new PaymentFailException(errorResponse.message(), e);
        } catch (Exception e) {
            throw new PaymentFailException("결제 승인 요청 중 오류가 발생했습니다.", e);
        }
    }
}
