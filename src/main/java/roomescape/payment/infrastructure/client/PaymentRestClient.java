package roomescape.payment.infrastructure.client;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.ClientFailException.PaymentClientFailException;
import roomescape.payment.model.PaymentClient;
import roomescape.reservation.model.vo.PaymentInfo;

@RequiredArgsConstructor
public class PaymentRestClient implements PaymentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Override
    public void requestApprove(final PaymentInfo paymentInfo) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(APPLICATION_JSON)
                .body(paymentInfo)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    PaymentErrorResponse errorResponse = objectMapper.readValue(errorBody, PaymentErrorResponse.class);
                    throw new PaymentClientFailException(errorResponse.message(), response.getStatusCode().value());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new PaymentClientFailException("내부 시스템처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.",
                            response.getStatusCode().value());
                })
                .toBodilessEntity();
    }
}
