package roomescape.payment.toss;

import java.net.SocketTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.InternalServerException;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;

@RequiredArgsConstructor
public class TossPaymentRestClient implements TossPaymentClient {

    private final RestClient restClient;

    @Override
    public TossPaymentResponse requestPaymentApprove(final TossPaymentRequest request) {
        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .body(request)
                    .retrieve()
                    .body(TossPaymentResponse.class);
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new InternalServerException("TIME_OUT_EXCEPTION", "요청 시간이 초과되었습니다.");
            }
            throw new InternalServerException("CONNECTION_EXCEPTION", "연결에 실패하였습니다.");
        }
    }
}
