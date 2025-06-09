package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.PaymentClient;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;
import roomescape.payment.infrastructure.dto.TossErrorResponse;
import roomescape.payment.infrastructure.dto.TossPaymentRequest;
import roomescape.payment.infrastructure.dto.TossPaymentResponse;

@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(final RestClient tossPaymentRestClient,
                             final ObjectMapper objectMapper) {
        this.restClient = tossPaymentRestClient;
        this.objectMapper = objectMapper;
    }

    public PaymentResponse requestPaymentConfirm(final PaymentRequest request) {
        return restClient.post()
                .uri("v1/payments/confirm")
                .body(TossPaymentRequest.from(request))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> handleException(res))
                .body(TossPaymentResponse.class);
    }

    private void handleException(final ClientHttpResponse res) {
        try (InputStream is = res.getBody()) {
            TossErrorResponse error = objectMapper.readValue(is, TossErrorResponse.class);
            throw new TossPaymentException(error.code(), error.message());
        } catch (IOException e) {
            throw new TossPaymentException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "오류 응답을 읽는 중에 문제가 발생했습니다.");
        }
    }
}
