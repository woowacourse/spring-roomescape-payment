package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.PaymentClient;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;
import roomescape.payment.infrastructure.dto.TossErrorResponse;
import roomescape.payment.infrastructure.dto.TossPaymentResponse;

@RequiredArgsConstructor
@Component
public class TossPaymentClient implements PaymentClient {

    @Qualifier("tossPaymentRestClient")
    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    public PaymentResponse requestPayment(final PaymentRequest request) {
        return restClient.post()
                .uri("v1/payments/confirm")
                .body(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> {
                            handleException(res);
                        })
                .body(TossPaymentResponse.class);
    }

    private void handleException(final ClientHttpResponse res) {
        try (InputStream is = res.getBody()) {
            TossErrorResponse error = objectMapper.readValue(is, TossErrorResponse.class);
            throw new TossPaymentException(error.code(), error.message());
        } catch (IOException e) {
            throw new RuntimeException("에러 응답 파싱 실패", e);
        }
    }
}

