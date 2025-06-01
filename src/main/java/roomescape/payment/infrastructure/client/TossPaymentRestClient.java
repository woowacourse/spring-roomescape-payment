package roomescape.payment.infrastructure.client;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.global.exception.ClientFailException.PaymentClientFailException;
import roomescape.payment.model.PaymentClient;
import roomescape.reservation.model.vo.PaymentInfo;

@RequiredArgsConstructor
public class TossPaymentRestClient implements PaymentClient {

    @Value("${payment.toss.endpoints.confirm}")
    private String confirmUrl;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Override
    public void requestConfirm(final PaymentInfo paymentInfo) {
        restClient.post()
                .uri(confirmUrl)
                .contentType(APPLICATION_JSON)
                .body(paymentInfo)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, handleClientError())
                .onStatus(HttpStatusCode::is5xxServerError, handleServerError())
                .toBodilessEntity();
    }

    private ErrorHandler handleClientError() {
        return (request, response) -> {
            final PaymentErrorResponse errorResponse = getPaymentErrorResponse(response);
            final int statusCode = response.getStatusCode().value();
            throw new PaymentClientFailException(errorResponse.message(), statusCode);
        };
    }

    private ErrorHandler handleServerError() {
        return (request, response) -> {
            final int statusCode = response.getStatusCode().value();
            throw new PaymentClientFailException("내부 시스템처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.", statusCode);
        };
    }

    private PaymentErrorResponse getPaymentErrorResponse(final ClientHttpResponse response) throws IOException {
        final String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        return objectMapper.readValue(errorBody, PaymentErrorResponse.class);
    }
}
