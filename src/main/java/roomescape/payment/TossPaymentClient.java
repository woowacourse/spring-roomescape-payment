package roomescape.payment;

import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.global.exception.IllegalRequestException;
import roomescape.global.exception.PaymentFailException;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.dto.response.TossErrorResponse;

public class TossPaymentClient {

    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String KEY_DELIMITER = ":";

    private final RestClient restClient;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentConfirmResponse confirmPayments(PaymentConfirmRequest request) {
        String secretKey = WIDGET_SECRET_KEY + KEY_DELIMITER;
        try {
            return restClient.post().uri("/v1/payments/confirm")
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(secretKey.getBytes()))
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
