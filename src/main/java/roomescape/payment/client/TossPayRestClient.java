package roomescape.payment.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentFailException;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.CancelRequest;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.TossClientResponse;

public class TossPayRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TossPayRestClient.class);

    private final RestClient restClient;

    public TossPayRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Payment pay(PaymentRequest paymentRequest) {
        try {
            TossClientResponse response = restClient.post()
                    .uri("/v1/payments/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .body(TossClientResponse.class);
            return getPayment(response);
        } catch (ResourceAccessException exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw new PaymentFailException(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "요청 시간이 초과되었습니다. 잠시 후 다시 시도해주세요."
            );
        }
    }

    public Payment cancel(CancelRequest cancelRequest) {
        try {
            TossClientResponse response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/payments/{paymentKey}/cancel")
                            .build(cancelRequest.paymentKey()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cancelRequest)
                    .retrieve()
                    .body(TossClientResponse.class);
            return getPayment(response);
        } catch (ResourceAccessException exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw new PaymentFailException(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "요청 시간이 초과되었습니다. 잠시 후 다시 시도해주세요."
            );
        }
    }

    private Payment getPayment(TossClientResponse response) {
        if (response == null) {
            throw new PaymentFailException("NULL RESPONSE", "결제에 실패했습니다.");
        }
        return response.toPayment();
    }
}
