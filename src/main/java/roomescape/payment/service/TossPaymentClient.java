package roomescape.payment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
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

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentConfirmResponse confirmPayments(PaymentConfirmRequest request) {
        try {
            return restClient.post().uri(paymentConfirmEndpointV1)
                    .headers(headers -> headers.setBasicAuth(widgetSecretKey, password))
                    .body(request)
                    .retrieve()
                    .body(PaymentConfirmResponse.class);
        } catch (RestClientResponseException e) {
            TossErrorResponse errorResponse = e.getResponseBodyAs(TossErrorResponse.class);
            throw new PaymentFailException(errorResponse.code(), errorResponse.message(), e);
        } catch (Exception e) {
            throw new PaymentFailException("결제 승인 요청 중 오류가 발생했습니다.", e);
        }
    }
}
